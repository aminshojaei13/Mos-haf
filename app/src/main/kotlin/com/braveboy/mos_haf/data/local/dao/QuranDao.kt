package com.braveboy.mos_haf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.braveboy.mos_haf.data.local.entity.QuranCleanTextEntity
import com.braveboy.mos_haf.data.local.entity.QuranEntity
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

    @Query("SELECT * FROM quran_text WHERE sura_name = :suraName ORDER BY aya")
    fun getVersesBySura(suraName: String): List<QuranEntity>

    @Query("SELECT * FROM quran_text WHERE page = :pageNumber ORDER BY `index`")
    fun getVersesByPage(pageNumber: Int): Flow<List<QuranEntity>>

    @Query("""
        SELECT * FROM quran_text 
        WHERE (`index` >= (SELECT `index` FROM quran_text WHERE sura = :startSura AND aya = :startAya LIMIT 1))
        AND (`index` <= (SELECT `index` FROM quran_text WHERE sura = :endSura AND aya = :endAya LIMIT 1))
        ORDER BY `index`
    """)
    fun getVersesByDetailedRange(startSura: Int, startAya: Int, endSura: Int, endAya: Int): List<QuranEntity>

    @Query("SELECT * FROM quran_clean_text ORDER BY id")
    fun getAllQuranCleanText(): Flow<List<QuranCleanTextEntity>>

    @Query("SELECT COUNT(*) FROM quran_text")
    fun getQuranCount(): Int

    @Query(" SELECT DISTINCT sura_name FROM quran_text ORDER BY sura ASC")
    fun getAllSura(): List<String>

    @Query(" SELECT DISTINCT * FROM quran_text ORDER BY sura ASC")
    fun getAllSuraWithDetail(): List<QuranEntity>

    @Query("SELECT COUNT(*) FROM quran_text GROUP BY sura ORDER BY sura")
    fun getAyaCounts(): List<Int>

    @Query(" SELECT trtext FROM suretranslate WHERE sura = :suraNumber ORDER BY aya")
    fun getSuraTranslate(suraNumber: Int): List<String>

    @Query("""
        SELECT trtext FROM suretranslate 
        WHERE (`id` >= (SELECT `id` FROM suretranslate WHERE sura = :startSura AND aya = :startAya LIMIT 1))
        AND (`id` <= (SELECT `id` FROM suretranslate WHERE sura = :endSura AND aya = :endAya LIMIT 1))
        ORDER BY `id`
    """)
    fun getByTranslateRange(startSura: Int, startAya: Int, endSura: Int, endAya: Int): List<String>

    @Query("""
        SELECT * FROM quran_text 
        WHERE juz = :joz AND hezb = :hezb
        ORDER BY `index`
    """)
    fun getByJozAndHezb(
        joz: Int,
        hezb: Int,
    ): List<QuranEntity>

    @Query("""
        SELECT * FROM quran_text 
        WHERE juz = :joz
        ORDER BY `index`
    """)
    fun getByJoz(
        joz: Int,
    ): List<QuranEntity>

    @Query("""
        SELECT * FROM quran_text 
        WHERE hezb = :hezb
        ORDER BY `index`
    """)
    fun getByHezb(
        hezb: Int,
    ): List<QuranEntity>

    @Query("""
        SELECT * FROM quran_text 
        WHERE page = :page
        ORDER BY `index`
    """)
    fun getByPage(
        page: Int,
    ): List<QuranEntity>
}