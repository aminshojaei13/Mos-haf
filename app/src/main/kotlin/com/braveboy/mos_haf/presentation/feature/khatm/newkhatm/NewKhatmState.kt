package com.braveboy.mos_haf.presentation.feature.khatm.newkhatm

data class NewKhatmState (
    val suraNames: List<String> = emptyList(),
    val ayaCounts: List<Int> = emptyList(),
    val error: String? = null,
)