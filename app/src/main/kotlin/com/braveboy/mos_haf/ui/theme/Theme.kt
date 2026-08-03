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

private val SepiaColorScheme = lightColorScheme(
    primary = Color(0xFF8D6E63),
    secondary = Color(0xFFD7CCC8),
    tertiary = Color(0xFF5D4037),
    background = SepiaBackground,
    surface = SepiaSurface,
    onPrimary = Color.White,
    onSecondary = SepiaText,
    onTertiary = Color(0xFF795548),
    onBackground = SepiaText,
    onSurface = SepiaText,
)

private val DarkBlueColorScheme = darkColorScheme(
    primary = Color(0xFF415A77),
    secondary = Color(0xFF778DA9),
    tertiary = Color(0xFF1B263B),
    background = DarkBlueBackground,
    surface = DarkBlueSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFFE0E1DD),
    onBackground = DarkBlueText,
    onSurface = DarkBlueText,
)

enum class ThemeType {
    LIGHT, DARK, SEPIA, DARK_BLUE
}

@Composable
fun MoshafTheme(
    themeType: ThemeType = ThemeType.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.LIGHT -> LightColorScheme
        ThemeType.DARK -> DarkColorScheme
        ThemeType.SEPIA -> SepiaColorScheme
        ThemeType.DARK_BLUE -> DarkBlueColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = 
                themeType == ThemeType.LIGHT || themeType == ThemeType.SEPIA
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
