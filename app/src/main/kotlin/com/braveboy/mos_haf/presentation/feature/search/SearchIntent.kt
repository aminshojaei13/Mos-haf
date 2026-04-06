package com.braveboy.mos_haf.presentation.feature.search

sealed class SearchIntent {
    data class LoadVersesByDetailedRange(
        val startSura: Int,
        val startAya: Int,
        val endSura: Int,
        val endAya: Int
    ) : SearchIntent()

    data class LoadVersesByJozAndHezb(
        val joz: Int,
        val hezb: Int,
    ) : SearchIntent()

    data class LoadVersesBySura(
        val sura: Int,
    ) : SearchIntent()

    object LoadSuraNames : SearchIntent()
    object RefreshData : SearchIntent()
}
