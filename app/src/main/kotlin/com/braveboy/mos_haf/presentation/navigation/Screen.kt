package com.braveboy.mos_haf.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    object Home : Screen("home_screen")

    @Serializable
    data class Search(val fromLast: Boolean) : Screen("search_screen")

    @Serializable
    data class QuranDetail(val sura: String, val fromLast: Boolean) : Screen("quran_detail_screen")

    @Serializable
    object SuraList : Screen("sura_list_screen")

    @Serializable
    object KhatmHome : Screen("khatm_home_screen")

    @Serializable
    data class KhatmDetail(val khatmId: Int) : Screen("new_khatm_screen")

    @Serializable
    data class KhatmVerses(val fromLast: Boolean, val khatm: String) :
        Screen("khatm_verses_screen")

}