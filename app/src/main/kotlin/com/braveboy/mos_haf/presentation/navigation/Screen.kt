package com.braveboy.mos_haf.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    object Home: Screen("home_screen")
    @Serializable
    object Search: Screen("search_screen")
    @Serializable
    object Quran: Screen("Quran_screen")
}