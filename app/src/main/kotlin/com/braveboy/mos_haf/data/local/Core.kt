package com.braveboy.mos_haf.data.local

data class HezbTime(
    val hezb: Int,
    val endSeconds: Double
)

data class HezbTimingData(
    val hezbs: List<HezbTime> = emptyList()
)