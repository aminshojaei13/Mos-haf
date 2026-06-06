package com.braveboy.mos_haf.presentation.feature.player.data

sealed class PlayType {
    data class AYAH(val surah: Int, val ayah: Int) : PlayType()
    data class PLAYLIST(val ayahs: List<Pair<Int, Int>>, val startIndex: Int = 0) : PlayType()

}