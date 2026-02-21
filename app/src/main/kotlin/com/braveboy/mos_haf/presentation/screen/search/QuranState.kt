package com.braveboy.mos_haf.presentation.screen.search

import com.braveboy.mos_haf.domain.model.Quran

data class QuranState(
    val isLoading: Boolean = false,
    val verses: List<Quran> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val totalVerses: Int = 0
)