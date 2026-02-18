package com.braveboy.mos_haf

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuranRepository(private val localDataSource: LocalDataSource) {
    
    suspend fun insertQuranVerses(verses: List<QuranEntity>) {
        localDataSource.insertAllQuranVerses(verses)
    }
    
    suspend fun insertQuranCleanText(verses: List<QuranCleanTextEntity>) {
        localDataSource.insertAllQuranCleanText(verses)
    }
    
    fun getAllQuranVerses(): Flow<List<Quran>> = 
        localDataSource.getAllQuranVerses().map { entities ->
            entities.map { it.toDomain() }
        }
    
    fun getVersesBySura(suraNumber: Int): Flow<List<Quran>> = 
        localDataSource.getVersesBySura(suraNumber).map { entities ->
            entities.map { it.toDomain() }
        }
    
    fun getVersesByPage(pageNumber: Int): Flow<List<Quran>> = 
        localDataSource.getVersesByPage(pageNumber).map { entities ->
            entities.map { it.toDomain() }
        }
    
    fun getAllQuranCleanText(): Flow<List<QuranCleanText>> = 
        localDataSource.getAllQuranCleanText().map { entities ->
            entities.map { it.toDomain() }
        }
    
    suspend fun getQuranCount(): Int = localDataSource.getQuranCount()
    
    private fun QuranEntity.toDomain(): Quran = Quran(
        id = id,
        sura = sura,
        aya = aya,
        text = text,
        suraName = suraName,
        page = page,
        juz = juz,
        hezb = hezb
    )
    
    private fun QuranCleanTextEntity.toDomain(): QuranCleanText = QuranCleanText(
        id = id,
        sura = sura,
        aya = aya,
        text = text
    )
}