package com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail

import com.braveboy.mos_haf.data.local.entity.KhatmEntity

data class KhatmDetailState (
    val khatmId: Int? = null,
    val khatmDetail: KhatmEntity? = null,
    val suraNames: List<String> = emptyList(),
    val ayaCounts: List<Int> = emptyList(),
    val error: String? = null,
)