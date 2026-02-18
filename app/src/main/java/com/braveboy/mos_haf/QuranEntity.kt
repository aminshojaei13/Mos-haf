package com.braveboy.mos_haf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_text")
data class QuranEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "index")
    val id: Int = 0,
    
    @ColumnInfo(name = "sura")
    val sura: Int,
    
    @ColumnInfo(name = "aya")
    val aya: Int,
    
    @ColumnInfo(name = "text")
    val text: String,
    
    @ColumnInfo(name = "sura_name")
    val suraName: String?,
    
    @ColumnInfo(name = "page")
    val page: Int,
    
    @ColumnInfo(name = "juz")
    val juz: Int,
    
    @ColumnInfo(name = "hezb")
    val hezb: Int
)