package com.braveboy.mos_haf

data class QuranState(
    val isLoading: Boolean = false,
    val verses: List<Quran> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val totalVerses: Int = 0
)