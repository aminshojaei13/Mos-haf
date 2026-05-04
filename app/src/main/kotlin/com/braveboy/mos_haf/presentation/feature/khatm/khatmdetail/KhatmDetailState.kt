package com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail

import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.domain.model.Quran

data class KhatmDetailState (
    val khatmId: Int? = null,
    val khatmDetail: KhatmEntity? = null,
    val suraNames: List<Quran> = emptyList(),
    val ayaCounts: List<Int> = emptyList(),
    val error: String? = null,
)