package com.braveboy.mos_haf.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LastReadModel(
    val source: String?,
    val start: Quran?,
    val end: Quran?
)