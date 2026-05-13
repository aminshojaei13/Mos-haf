package com.braveboy.mos_haf.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green800,
    secondary = Green100,
    tertiary = Green600,
    background = Cream,
    surface = FloralWhite,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = NewYellow,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Green600,
    secondary = Green100,
    tertiary = Green800,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = NewOrange,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun MoshafTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    var colorScheme by remember { mutableStateOf(LightColorScheme) }
    LaunchedEffect(darkTheme) {
        colorScheme = when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
