package com.braveboy.mos_haf.presentation.feature.player.model

data class PlayerState(
    val currentPage: Int = 1,
    val totalPages: Int = 604,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPosition: Long = 0,
    val bufferedPercentage: Int = 0,
    val duration: Long = 0,
    val errorMessage: String? = null,
    val playbackSpeed: Float = 1.0f,
    val hezbStartTime: Long = 0L,
    val hezbEndTime: Long = 0L,
    val playlist: List<Pair<Int, Int>> = emptyList(),  // لیست (سوره، آیه)
    val currentPlaylistIndex: Int = 0,                 // index آیه در حال پخش
    val currentAyahText: String = ""
)