package com.braveboy.mos_haf.presentation.feature.player.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import com.braveboy.mos_haf.data.repository.HezbTimeRepository
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import com.braveboy.mos_haf.presentation.feature.player.data.PlayerRepository
import com.braveboy.mos_haf.presentation.feature.player.model.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewController(
    private val repository: PlayerRepository,
    private val getHezbTimeRepository: HezbTimeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _effect = Channel<PlayerEffect>()
    val effect = _effect.receiveAsFlow()

    private var exoPlayer: ExoPlayer? = null
    private var positionUpdateJob: Job? = null

    fun setExoPlayer(player: ExoPlayer) {
        this.exoPlayer = player
        player.setPlaybackSpeed(1.0F)
        observePlayerEvents()
    }

    private fun startPositionUpdater() {
        stopPositionUpdater()
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                delay(500)
                exoPlayer?.let { player ->
                    _state.update { currentState ->
                        currentState.copy(
                            currentPosition = player.currentPosition,
                            bufferedPercentage = player.bufferedPercentage,
                            isPlaying = player.isPlaying
                        )
                    }
                }
            }
        }
    }

    private fun stopPositionUpdater() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private fun observePlayerEvents() {
        exoPlayer?.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                _state.update { it.copy(duration = exoPlayer?.duration ?: 0) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> _state.update { it.copy(isLoading = true) }
                    Player.STATE_READY -> {
                        _state.update {
                            Log.d(
                                "toni",
                                "observePlayerEvents: ${exoPlayer?.playlistMetadata?.durationMs}"
                            )
                            it.copy(
                                isLoading = false,
                            )
                        }
                        startPositionUpdater()
                    }

                    Player.STATE_ENDED -> {
                        _state.update { it.copy(isPlaying = false) }
                        stopPositionUpdater()
                    }

                    Player.STATE_IDLE -> {
                        _state.update { it.copy(isLoading = false, isPlaying = false) }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startPositionUpdater() else stopPositionUpdater()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentIndex = exoPlayer?.currentMediaItemIndex ?: 0
                _state.update { it.copy(currentPlaylistIndex = currentIndex) }
            }
        })
    }

    fun handleIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.Load -> load(type = intent.type)
            PlayerIntent.Play -> play()
            PlayerIntent.Pause -> pause()
            is PlayerIntent.SeekTo -> seekTo(intent.position)
            is PlayerIntent.ChangeSpeed -> changeSpeed(intent.speed)
            PlayerIntent.Release -> releasePlayer()
            PlayerIntent.NextTrack -> nextTrack()
            PlayerIntent.PreviousTrack -> previousTrack()
            else -> {}
        }
    }

    private fun load(type: PlayType) {
        if (type is PlayType.PLAYLIST) {
            val ayahs = type.ayahs.filter { it.second != 0 }
            if (ayahs.isEmpty()) {
                viewModelScope.launch { _effect.send(PlayerEffect.ShowError("لیست آیات خالی است")) }
                return
            }

            _state.update { it.copy(playlist = ayahs, currentPlaylistIndex = 0) }

            viewModelScope.launch(Dispatchers.Main) {
                try {
                    val mediaItems = ayahs.map { (surah, ayah) ->
                        MediaItem.fromUri(repository.getAyahUrl(surah, ayah))
                    }
                    exoPlayer?.apply {
                        setMediaItems(mediaItems)
                        prepare()
                    }
                } catch (e: Exception) {
                    _effect.send(PlayerEffect.ShowError("خطا در بارگذاری لیست پخش"))
                }
            }
        }
    }

    private fun play() {
        exoPlayer?.play()
    }

    private fun pause() {
        exoPlayer?.pause()
    }

    private fun nextTrack() {
        exoPlayer?.let {
            if (it.hasNextMediaItem()) {
                it.seekToNextMediaItem()
            } else {
                viewModelScope.launch { _effect.send(PlayerEffect.ShowError("آیه بعدی وجود ندارد")) }
            }
        }
    }

    private fun previousTrack() {
        exoPlayer?.let {
            if (it.hasPreviousMediaItem()) {
                it.seekToPreviousMediaItem()
            } else {
                viewModelScope.launch { _effect.send(PlayerEffect.ShowError("آیه قبلی وجود ندارد")) }
            }
        }
    }

    private fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    private fun changeSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        _state.update { it.copy(playbackSpeed = speed) }
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
        stopPositionUpdater()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}
