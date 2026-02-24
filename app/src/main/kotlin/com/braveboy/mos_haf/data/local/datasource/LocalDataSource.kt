package com.braveboy.mos_haf.data.local.datasource

import android.util.Log
import com.braveboy.mos_haf.data.local.entity.QuranCleanTextEntity
import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.data.local.entity.QuranEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count

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

    fun getVersesBySura(suraName: String): List<QuranEntity> {
        Log.d("xavi2", "getSuraByName: ${database.quranDao().getVersesBySura(suraName)}")
        return database.quranDao().getVersesBySura(suraName)
    }

    fun getVersesByPage(pageNumber: Int): Flow<List<QuranEntity>> =
        database.quranDao().getVersesByPage(pageNumber)

    fun getAllQuranCleanText(): Flow<List<QuranCleanTextEntity>> =
        database.quranDao().getAllQuranCleanText()

    fun getQuranCount(): Int = database.quranDao().getQuranCount()

    fun getAllSura(): List<String> = database.quranDao().getAllSura()

    fun getSuraTranslate(suraNumber: Int): List<String> {
        Log.d("xavi", "saveSssss: ${database.quranDao().getSuraTranslate(suraNumber)}")
      return database.quranDao().getSuraTranslate(suraNumber)
    }

}