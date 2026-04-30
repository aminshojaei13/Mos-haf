package com.braveboy.mos_haf.presentation.feature.khatm.home

import com.braveboy.mos_haf.data.local.entity.KhatmEntity

data class KhatmHomeState(
    val khatms: List<KhatmEntity> = emptyList()
)
