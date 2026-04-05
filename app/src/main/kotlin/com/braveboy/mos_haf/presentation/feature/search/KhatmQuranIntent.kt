package com.braveboy.mos_haf.presentation.feature.search

sealed class KhatmQuranIntent {
    data class LoadVersesByDetailedRange(
        val startSura: Int,
        val startAya: Int,
        val endSura: Int,
        val endAya: Int
    ) : KhatmQuranIntent()

    data class LoadVersesByJozAndHezb(
        val joz: Int,
        val hezb: Int,
    ) : KhatmQuranIntent()

    data class LoadVersesBySura(
        val sura: Int,
    ) : KhatmQuranIntent()

    object LoadSuraNames : KhatmQuranIntent()
    object RefreshData : KhatmQuranIntent()
}
