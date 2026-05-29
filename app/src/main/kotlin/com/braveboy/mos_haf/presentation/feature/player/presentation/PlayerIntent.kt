package com.braveboy.mos_haf.presentation.feature.player.presentation

import com.braveboy.mos_haf.presentation.feature.player.data.PlayType

sealed class PlayerIntent {
    data class Load(
        val type: PlayType,
        val number: Int
    ) : PlayerIntent()

    object Play : PlayerIntent()
    object Pause : PlayerIntent()
    object NextPage : PlayerIntent()
    object PreviousPage : PlayerIntent()
    data class SeekTo(val position: Long) : PlayerIntent()
    data class ChangeSpeed(val speed: Float) : PlayerIntent()
    object Release : PlayerIntent()

    object NextTrack : PlayerIntent()      // <-- جدید
    object PreviousTrack : PlayerIntent()  // <-- جدید
}