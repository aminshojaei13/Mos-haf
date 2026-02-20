package com.braveboy.mos_haf.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.braveboy.mos_haf.presentation.screen.search.QuranScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavHost(
            navController = navController,
            startDestination = "quran",
        ) {
            composable("quran") {
                QuranScreen()
            }
        }
    }
}