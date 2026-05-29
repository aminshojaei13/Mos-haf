package com.braveboy.mos_haf.presentation.feature.khatm.khatmverses

import android.content.Context

sealed class KhatmVersesIntent {
    data class LoadAnotherVerse(val id: Int) : KhatmVersesIntent()
    data object SaveCompleteReadPage : KhatmVersesIntent()
}