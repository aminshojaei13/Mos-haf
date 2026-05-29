package com.braveboy.mos_haf.presentation.feature.player.presentation

sealed class PlayerEffect {
    data class ShowError(val message: String) : PlayerEffect()
    data class PageChanged(val page: Int) : PlayerEffect()
}