package com.braveboy.mos_haf.presentation.feature.detail

import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.model.Quran

data class QuranDetailState(
    val isFromLast: Boolean = false,
    val lastRead: LastReadModel? = null,
    val verses: List<Quran> = emptyList(),
    val suraNames: List<String> = emptyList(),
    val translations: List<String> = emptyList(),
    val suraName: String = "",
    val fontSize: Float? = null,
    val isLoading: Boolean = true
)