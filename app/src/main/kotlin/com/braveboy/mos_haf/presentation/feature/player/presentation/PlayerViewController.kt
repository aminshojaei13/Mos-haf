package com.braveboy.mos_haf.presentation.feature.player.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

    // State (وضعیت جاری)
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    // Effect (رویدادهای یکبار مصرف)
    private val _effect = Channel<PlayerEffect>()
    val effect = _effect.receiveAsFlow()

    private var exoPlayer: ExoPlayer? = null
    private var positionUpdateJob: Job? = null

    init {
        initializePlayer()
        observePlayerEvents()
    }

    private fun initializePlayer() {
        // ExoPlayer بعداً با context مقداردهی می‌شود
        // از طریق تابع setExoPlayer از Activity فراخوانی می‌شود
    }

    fun setExoPlayer(player: ExoPlayer) {
        this.exoPlayer = player
        player.setPlaybackSpeed(1.0F)
        observePlayerEvents()
    }

    private fun startPositionUpdater() {
        stopPositionUpdater() // متوقف کردن تایمر قبلی اگر وجود دارد

        positionUpdateJob = viewModelScope.launch {
            while (true) {
                delay(500) // هر 500 میلی‌ثانیه به‌روزرسانی
                exoPlayer?.let { player ->
                    val currentPosition = player.currentPosition
                    val bufferedPercentage = player.bufferedPercentage
                    val isPlaying = player.isPlaying

                    _state.update { currentState ->
                        currentState.copy(
                            currentPosition = currentPosition,
                            bufferedPercentage = bufferedPercentage,
                            isPlaying = isPlaying
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
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    Player.STATE_READY -> {
                        play()
                        startPositionUpdater()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                duration = exoPlayer?.duration ?: 0
                            )
                        }
                    }

                    Player.STATE_ENDED -> {
                        _state.update { it.copy(isPlaying = false) }
                        stopPositionUpdater()
                    }

                    Player.STATE_IDLE -> {
                        Log.e(
                            "xavi",
                            "onPlaybackStateChanged: not handle Player.STATE_IDLE in PlayerViewController",
                        )
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                Log.d("toni", "ddd: ${newPosition}")
                _state.update {
                    it.copy(currentPosition = exoPlayer?.currentPosition ?: 0)
                }
            }
        })
    }

    // دریافت Intent از View
    fun handleIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.Load -> load(type = intent.type)
            PlayerIntent.Play -> playAyahFromPlaylist(0)
            PlayerIntent.Pause -> pause()
            PlayerIntent.NextPage -> nextPage()
            PlayerIntent.PreviousPage -> previousPage()
            is PlayerIntent.SeekTo -> seekTo(intent.position)
            is PlayerIntent.ChangeSpeed -> changeSpeed(intent.speed)
            PlayerIntent.Release -> releasePlayer()
            else -> {}
        }
    }

    private fun load(type: PlayType) {
        when (type) {
            /*PlayType.JOZ -> {
                if (number !in 1..30) {
                    viewModelScope.launch {
                        _effect.send(PlayerEffect.ShowError("جز نامعتبر است (۱ تا ۳۰)"))
                    }
                    return
                }
                val url = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/260.mp3"
                val mediaItem = MediaItem.fromUri(url)

                exoPlayer?.apply {
                    setMediaItem(mediaItem)
                    prepare()
                }
            }

            PlayType.HEZB -> {
                loadHezbTime(number)

                if (number !in 1..120) {
                    viewModelScope.launch {
                        _effect.send(PlayerEffect.ShowError("حزب نامعتبر است (۱ تا ۱۲۰)"))
                    }
                    return
                }
            }

            PlayType.PAGE -> {
                if (number !in 1..604) {
                    viewModelScope.launch {
                        _effect.send(PlayerEffect.ShowError("صفحه نامعتبر است (۱ تا ۶۰۴)"))
                    }
                    return
                }
                val url = repository.getPageUrl(number)
                val mediaItem = MediaItem.fromUri(url)

                exoPlayer?.apply {
                    setMediaItem(mediaItem)
                    prepare()
                }
            }

            PlayType.SURAH -> {
                if (number !in 1..114) {
                    viewModelScope.launch {
                        _effect.send(PlayerEffect.ShowError("سوره نامعتبر است (۱ تا ۱۱۴)"))
                    }
                    return
                }
                val url = repository.getSurahUrl(number)
                val mediaItem = MediaItem.fromUri(url)

                exoPlayer?.apply {
                    setMediaItem(mediaItem)
                    prepare()
                }
            }*/
            is PlayType.AYAH -> {}
            is PlayType.PLAYLIST -> {
                val ayahs = mutableListOf(Pair(1,1))

                Log.d("toni", "load: ${type.ayahs}")
                type.ayahs.forEach {
                    if (it.second != 1){
                        ayahs.add(it)
                    }
                }
                Log.d("toni", "load2: $ayahs")
                if (ayahs.isEmpty()) {
                    viewModelScope.launch {
                        _effect.send(PlayerEffect.ShowError("لیست آیات خالی است"))
                    }
                    return
                }

                // ذخیره پلی‌لیست در state
                _state.update {
                    it.copy(
                        playlist = ayahs,
                        currentPlaylistIndex = 0
                    )
                }
            }
        }
    }

    private fun playAyahFromPlaylist(index: Int) {
        val playlist = _state.value.playlist
        if (index !in playlist.indices) {
            // پایان پلی‌لیست
            _state.update {
                it.copy(
                    isPlaying = false,
                    isLoading = false
                )
            }
            viewModelScope.launch {
                _effect.send(PlayerEffect.ShowError("پایان پلی‌لیست"))
            }
            return
        }

        val (surah, ayah) = playlist[index]

        // به‌روزرسانی state
        _state.update {
            it.copy(
                currentPlaylistIndex = index,
                isLoading = true,
                isPlaying = false
            )
        }

        // بارگذاری و پخش آیه
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val url = playlist.map {
                    repository.getAyahUrl(it.first, it.second)
                }

                val mediaItem = url.map {
                    MediaItem.fromUri(it)
                }
                exoPlayer?.apply {
                    setMediaItems(mediaItem)
                    prepare()
                    // توجه: play() در onPlaybackStateChanged بعد از STATE_READY انجام می‌شود
                }
            } catch (e: Exception) {
                Log.d("toni", "playAyahFromPlaylist: $e")
                viewModelScope.launch(Dispatchers.Main) {
                    _effect.send(PlayerEffect.ShowError("خطا در پخش آیه ${surah}:${ayah}"))
                    // پخش آیه بعدی
                    playNextInPlaylist()
                }
            }
        }
    }

    // تابع جدید: پخش آیه بعدی در پلی‌لیست
    private fun playNextInPlaylist() {
        Log.d("toni", "playNextInPlaylist: ${_state.value.currentPlaylistIndex + 1}")
        val nextIndex = _state.value.currentPlaylistIndex + 1
        playAyahFromPlaylist(nextIndex)
    }

    // تابع جدید: پخش آیه قبلی در پلی‌لیست
    private fun playPreviousInPlaylist() {
        val prevIndex = _state.value.currentPlaylistIndex - 1
        if (prevIndex >= 0) {
            playAyahFromPlaylist(prevIndex)
        }
    }

    private fun play() {
        exoPlayer?.setPlaybackSpeed(1f)
        exoPlayer?.play()
        _state.update { it.copy(isPlaying = true) }
    }

    private fun pause() {
        exoPlayer?.pause()
        _state.update { it.copy(isPlaying = false) }
    }

    private fun nextPage() {
        /*val nextPage = _state.value.currentPage + 1
        if (nextPage <= 604) {
            load(nextPage)
        } else {
            viewModelScope.launch {
                _effect.send(PlayerEffect.ShowError("به انتهای قرآن رسیدید"))
            }
        }*/
    }

    private fun previousPage() {
        /*val prevPage = _state.value.currentPage - 1
        if (prevPage >= 1) {
            load(prevPage)
        } else {
            viewModelScope.launch {
                _effect.send(PlayerEffect.ShowError("در صفحه اول قرآن هستید"))
            }
        }*/
    }

    private fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _state.update { it.copy(currentPosition = position) }
    }

    private fun changeSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        _state.update { it.copy(playbackSpeed = speed) }
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun loadHezbTime(number: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val hezbTime =
                getHezbTimeRepository.getHezbStartAndEndInMillis(number)
            _state.update { it.copy(hezbStartTime = hezbTime.first, hezbEndTime = hezbTime.second) }

            val url = repository.getJozUrl((number / 4) + 1)
            val clippingConfig = MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(hezbTime.first)
                .setEndPositionMs(hezbTime.second)
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .setClippingConfiguration(clippingConfig)
                .build()

            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                //setPlaybackSpeed(1.0F)
            }
        }
    }

}