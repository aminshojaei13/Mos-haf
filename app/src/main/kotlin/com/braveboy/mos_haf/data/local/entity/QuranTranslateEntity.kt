package com.braveboy.mos_haf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suretranslate")
data class QuranTranslateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "sura")
    val sura: Int,

    @ColumnInfo(name = "aya")
    val aya: Int,

    @ColumnInfo(name = "trtext")
    val text: String,


)