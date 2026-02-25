package com.braveboy.mos_haf.presentation.feature.detail

import com.braveboy.mos_haf.domain.model.Quran

data class QuranDetailState(
    val verses: List<Quran> = emptyList(),
    val translations: List<String> = emptyList(),
    val suraName: String = "",
    val isLoading: Boolean = true
)