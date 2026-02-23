package com.braveboy.mos_haf.presentation.feature.search

sealed class QuranIntent {
    object LoadAllVerses : QuranIntent()
    data class LoadVersesBySura(val suraNumber: Int) : QuranIntent()
    data class LoadVersesByPage(val pageNumber: Int) : QuranIntent()
    object RefreshData : QuranIntent()
}