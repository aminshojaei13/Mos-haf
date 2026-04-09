package com.braveboy.mos_haf.data.local.datasource

import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.data.local.entity.QuranCleanTextEntity
import com.braveboy.mos_haf.data.local.entity.QuranEntity
import com.braveboy.mos_haf.domain.model.Quran
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val database: AppDatabase) {

    // Quran methods
    suspend fun insertAllQuranVerses(verses: List<QuranEntity>) {
        database.quranDao().insertAllQuranVerses(verses)
    }

    suspend fun insertAllQuranCleanText(verses: List<QuranCleanTextEntity>) {
        database.quranDao().insertAllQuranCleanText(verses)
    }

    fun getAllQuranVerses(): Flow<List<QuranEntity>> = database.quranDao().getAllQuranVerses()

    fun getVersesBySura(suraNumber: Int): Flow<List<QuranEntity>> =
        database.quranDao().getVersesBySura(suraNumber)

    fun getVersesBySura(suraName: String): List<QuranEntity> = database.quranDao().getVersesBySura(suraName)

    fun getVersesByPage(pageNumber: Int): Flow<List<QuranEntity>> =
        database.quranDao().getVersesByPage(pageNumber)

    fun getVersesByDetailedRange(
        startSura: Int,
        startAya: Int,
        endSura: Int,
        endAya: Int
    ): List<QuranEntity> = database.quranDao().getVersesByDetailedRange(startSura, startAya, endSura, endAya)

    fun getAllQuranCleanText(): Flow<List<QuranCleanTextEntity>> =
        database.quranDao().getAllQuranCleanText()

    fun getQuranCount(): Int = database.quranDao().getQuranCount()

    fun getAllSura(): List<String> = database.quranDao().getAllSura()

    fun getAllSuraWithDetail(): List<QuranEntity> = database.quranDao().getAllSuraWithDetail()

    fun getAyaCounts(): List<Int> = database.quranDao().getAyaCounts()

    fun getSuraTranslate(suraNumber: Int): List<String> = database.quranDao().getSuraTranslate(suraNumber)

    fun getByTranslateRange(startSura: Int, startAya: Int, endSura: Int, endAya: Int): List<String> =
        database.quranDao().getByTranslateRange(startSura,startAya,endSura,endAya)

    fun getByJozAndHezb(
        joz: Int,
        hezb: Int,
    ): List<QuranEntity> =
        database.quranDao().getByJozAndHezb(joz,hezb)
}
