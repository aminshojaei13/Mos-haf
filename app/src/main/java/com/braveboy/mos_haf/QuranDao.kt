package com.braveboy.mos_haf

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuranVerses(verses: List<QuranEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuranCleanText(verses: List<QuranCleanTextEntity>)
    
    @Query("SELECT * FROM quran_text ORDER BY `index`")
    fun getAllQuranVerses(): Flow<List<QuranEntity>>
    
    @Query("SELECT * FROM quran_text WHERE sura = :suraNumber ORDER BY aya")
    fun getVersesBySura(suraNumber: Int): Flow<List<QuranEntity>>
    
    @Query("SELECT * FROM quran_text WHERE page = :pageNumber ORDER BY `index`")
    fun getVersesByPage(pageNumber: Int): Flow<List<QuranEntity>>
    
    @Query("SELECT * FROM quran_clean_text ORDER BY id")
    fun getAllQuranCleanText(): Flow<List<QuranCleanTextEntity>>
    
    @Query("SELECT COUNT(*) FROM quran_text")
    suspend fun getQuranCount(): Int
}