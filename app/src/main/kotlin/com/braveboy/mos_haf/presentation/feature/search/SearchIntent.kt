package com.braveboy.mos_haf.presentation.feature.search

import com.braveboy.mos_haf.domain.model.LastReadModel

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

    data class SaveTheme(
        val isDark: Boolean,
    ) : SearchIntent()

    data class SaveBookmark(
        val lastRead: LastReadModel,
    ) : SearchIntent()

    object LoadSuraNames : SearchIntent()
    object RefreshData : SearchIntent()
}
