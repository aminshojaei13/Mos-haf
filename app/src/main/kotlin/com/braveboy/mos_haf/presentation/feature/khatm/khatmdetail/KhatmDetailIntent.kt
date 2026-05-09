package com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail

sealed class KhatmDetailIntent {
    data object LoadKhatmDetail : KhatmDetailIntent()
}