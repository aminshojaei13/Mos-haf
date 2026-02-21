package com.braveboy.mos_haf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_clean_text")
data class QuranCleanTextEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "sura")
    val sura: Int,

    @ColumnInfo(name = "aya")
    val aya: Int,

    @ColumnInfo(name = "text")
    val text: String
)