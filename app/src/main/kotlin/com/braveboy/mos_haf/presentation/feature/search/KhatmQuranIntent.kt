package com.braveboy.mos_haf.presentation.feature.search

sealed class KhatmQuranIntent {
    data class LoadVersesByDetailedRange(
        val startSura: Int,
        val startAya: Int,
        val endSura: Int,
        val endAya: Int
    ) : KhatmQuranIntent()

    object LoadSuraNames : KhatmQuranIntent()
    object RefreshData : KhatmQuranIntent()
}
