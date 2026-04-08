package com.braveboy.mos_haf.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quran(
    val id: Int,
    val sura: Int,
    val aya: Int,
    val text: String,
    val suraName: String?,
    val page: Int,
    val juz: Int,
    val hezb: Int
)

data class QuranCleanText(
    val id: Int,
    val sura: Int,
    val aya: Int,
    val text: String
)