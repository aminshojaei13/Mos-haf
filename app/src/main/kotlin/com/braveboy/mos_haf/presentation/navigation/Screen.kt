package com.braveboy.mos_haf.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    object Home: Screen("home_screen")
    @Serializable
    object Search: Screen("search_screen")
    @Serializable
    data class QuranDetail(val sura: String): Screen("quran_detail_screen")

    @Serializable
    object SuraList: Screen("sura_list_screen")
}