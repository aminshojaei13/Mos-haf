package com.braveboy.mos_haf.presentation.feature.khatm.home

sealed class KhatmHomeIntent {
    data class NewKhatm(
        val name: String,
        val type: String,
    ) : KhatmHomeIntent()
}