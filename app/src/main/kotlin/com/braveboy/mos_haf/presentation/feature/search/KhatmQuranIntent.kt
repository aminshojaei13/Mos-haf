package com.braveboy.mos_haf.presentation.feature.search

sealed class KhatmQuranIntent {
    object LoadAllVerses : KhatmQuranIntent()
    data class LoadVersesBySura(val suraNumber: Int) : KhatmQuranIntent()
    data class LoadVersesByPage(val pageNumber: Int) : KhatmQuranIntent()
    object RefreshData : KhatmQuranIntent()
}