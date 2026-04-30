package com.braveboy.mos_haf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("khatm_quran")
data class KhatmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val pagesPerDay: Int,
    val completedPages: Int,
    val startDate: Long,
    val lastReadDate: Long?,
    val isActive: Boolean
)