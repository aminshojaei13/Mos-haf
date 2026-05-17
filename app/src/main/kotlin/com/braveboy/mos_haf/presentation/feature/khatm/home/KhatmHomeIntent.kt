package com.braveboy.mos_haf.presentation.feature.khatm.home

import com.braveboy.mos_haf.data.local.entity.KhatmEntity

sealed class KhatmHomeIntent {
    data class NewKhatm(
        val name: String,
        val type: String,
    ) : KhatmHomeIntent()

    data class DeleteKhatm(
        val khatmEntity: KhatmEntity
    ) : KhatmHomeIntent()

    data object LoadKhatmDetail : KhatmHomeIntent()
}