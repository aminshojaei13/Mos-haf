package com.braveboy.mos_haf.presentation.feature.khatm.khatmverses

sealed class KhatmVersesIntent {
    data class LoadAnotherVerse(val id: Int) : KhatmVersesIntent()
}