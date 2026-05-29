package com.braveboy.mos_haf.presentation.feature.player.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.media3.exoplayer.ExoPlayer
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranPlayer(
    modifier: Modifier = Modifier,
    type: PlayType,
    id: Int,
) {
    val context = LocalContext.current
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    val controller = koinViewModel<PlayerViewController>()
    val state by controller.state.collectAsState()
    var showSpeedSelector by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableFloatStateOf(0f) }

    @SuppressLint("DefaultLocale")
    fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = (ms / (1000 * 60 * 60))
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    // ارسال ExoPlayer به ViewModel
    LaunchedEffect(Unit) {
        controller.setExoPlayer(exoPlayer)
    }

    // نمایش خطاها
    LaunchedEffect(Unit) {
        controller.effect.collect { effect ->
            when (effect) {
                is PlayerEffect.ShowError -> {
                    android.widget.Toast.makeText(
                        context,
                        effect.message,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }

                is PlayerEffect.PageChanged -> {
                    // می‌توانید صفحه جدید را لاگ کنید یا انیمیشن نمایش دهید
                }
            }
        }
    }

    // لود صفحه اول هنگام شروع
    LaunchedEffect(Unit) {
        controller.handleIntent(PlayerIntent.Load(type = type, number = id))
    }

    // تغییر سرعت (بهره از sliderState قبلی)
    LaunchedEffect(Unit) {
        // این بخش رو می‌تونید با تنظیمات سرعت در ViewModel هماهنگ کنید
    }

    // به‌روزرسانی خودکار موقعیت پخش (برای نمایش روی Slider)
    LaunchedEffect(state.isPlaying, isSeeking) {
        while (state.isPlaying && !isSeeking) {
            delay(500)
            // مقدار seekPosition به‌روز می‌شه اما فقط وقتی در حال Seek نیستیم
            if (!isSeeking && state.duration > 0) {
                seekPosition = state.currentPosition.toFloat() / state.duration.toFloat()
            }
        }
    }

    // کنترل‌های دستی
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(vertical = 8.dp)
    ) {
        // دکمه پخش/مکث و سرعت
        Row(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (state.isLoading) return@IconButton
                    if (state.isPlaying) {
                        controller.handleIntent(PlayerIntent.Pause)
                    } else {
                        controller.handleIntent(PlayerIntent.Play)
                    }
                }
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    state.isPlaying -> {
                        Icon(
                            imageVector = Icons.Outlined.PauseCircleOutline,
                            contentDescription = "Pause",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.Outlined.PlayCircleOutline,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            AnimatedVisibility(state.isPlaying) {
                IconButton(
                    onClick = { showSpeedSelector = true }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Speed,
                        contentDescription = "Speed",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            AnimatedVisibility(state.isPlaying) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // اسلایدر برای Seek دستی (عقب و جلو کردن صدا)
                    Slider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = if (isSeeking) seekPosition else {
                            state.currentPosition.toFloat() / state.duration.toFloat()
                        },
                        onValueChange = { newValue ->
                            isSeeking = true
                            seekPosition = newValue
                        },
                        onValueChangeFinished = {
                            // وقتی کاربر انگشت را برداشت، Seek انجام شود
                            val seekToMs = (seekPosition * state.duration).roundToInt().toLong()
                            controller.handleIntent(PlayerIntent.SeekTo(seekToMs))
                            isSeeking = false
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                        )
                    )

                    //Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(state.currentPosition),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )

                        Text(
                            text = formatTime(state.duration),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // DropdownMenu برای سرعت (می‌توانید کامل‌تر کنید)
        DropdownMenu(
            expanded = showSpeedSelector,
            onDismissRequest = { showSpeedSelector = false },
        ) {
            val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
            speeds.forEach { speed ->
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
}