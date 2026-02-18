package com.braveboy.mos_haf

sealed class QuranIntent {
    object LoadAllVerses : QuranIntent()
    data class LoadVersesBySura(val suraNumber: Int) : QuranIntent()
    data class LoadVersesByPage(val pageNumber: Int) : QuranIntent()
    object RefreshData : QuranIntent()
}