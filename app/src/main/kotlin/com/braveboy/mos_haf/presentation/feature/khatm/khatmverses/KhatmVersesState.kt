package com.braveboy.mos_haf.presentation.feature.khatm.khatmverses

import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.presentation.feature.khatm.model.KhatmVersesModel

data class KhatmVersesState(
    val isFromLast: Boolean = false,
    val isDarkMode: Boolean = false,
    val lastRead: LastReadModel? = null,
    val khatm: KhatmVersesModel? = null,
    val verses: List<Quran> = emptyList(),
    val suraNames: List<String> = emptyList(),
    val translations: List<String> = emptyList(),
    val suraName: String = "",
    val fontSize: Float? = null,
    val isLoading: Boolean = true
)