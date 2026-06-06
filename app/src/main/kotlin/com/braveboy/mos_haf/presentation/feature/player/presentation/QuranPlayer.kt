package com.braveboy.mos_haf.presentation.feature.player.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.outlined.PauseCircleOutline
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import com.braveboy.mos_haf.presentation.feature.player.data.provideCacheDataSource
import org.koin.androidx.compose.koinViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranPlayer(
    modifier: Modifier = Modifier,
    type: PlayType,
) {
    val context = LocalContext.current
    val controller = koinViewModel<PlayerViewController>()
    val state by controller.state.collectAsState()
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(provideCacheDataSource(context)))
            .build()
    }

    var showSpeedSelector by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableFloatStateOf(0f) }

    @SuppressLint("DefaultLocale")
    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    DisposableEffect(exoPlayer) {
        controller.setExoPlayer(exoPlayer)
        onDispose {
            controller.handleIntent(PlayerIntent.Release)
        }
    }

    LaunchedEffect(type) {
        controller.handleIntent(PlayerIntent.Load(type = type))
    }

    LaunchedEffect(state.isPlaying, isSeeking, state.currentPosition) {
        if (!isSeeking && state.duration > 0) {
            seekPosition = state.currentPosition.toFloat() / state.duration.toFloat()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                    shape = MaterialTheme.shapes.large
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed selector
                Box {
                    IconButton(onClick = { showSpeedSelector = true }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showSpeedSelector,
                        onDismissRequest = { showSpeedSelector = false }
                    ) {
                        listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                            DropdownMenuItem(
                                text = { Text("${speed}x") },
                                onClick = {
                                    controller.handleIntent(PlayerIntent.ChangeSpeed(speed))
                                    showSpeedSelector = false
                                }
                            )
                        }
                    }
                }

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { controller.handleIntent(PlayerIntent.PreviousTrack) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.NavigateBefore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (state.isLoading) return@IconButton
                            if (state.isPlaying) controller.handleIntent(PlayerIntent.Pause)
                            else controller.handleIntent(PlayerIntent.Play)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Outlined.PauseCircleOutline else Icons.Outlined.PlayCircleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { controller.handleIntent(PlayerIntent.NextTrack) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.NavigateNext,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Time display
                Text(
                    text = formatTime(state.currentPosition),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 11.sp
                )
            }

            // Slim slider
            AnimatedVisibility(
                visible = state.duration > 0,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Slider(
                    value = seekPosition.coerceIn(0f, 1f),
                    onValueChange = {
                        isSeeking = true
                        seekPosition = it
                    },
                    onValueChangeFinished = {
                        controller.handleIntent(PlayerIntent.SeekTo((seekPosition * state.duration).toLong()))
                        isSeeking = false
                    },
                    modifier = Modifier.height(24.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}
