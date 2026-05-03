package com.braveboy.mos_haf.presentation.feature.khatm.model

import kotlinx.serialization.Serializable

@Serializable
data class KhatmVersesModel(
    val type: String,
    val joz: Int? = null,
    val hezb: Int? = null,
    val page: Int? = null,
)
