package com.braveboy.mos_haf

import kotlinx.coroutines.flow.Flow

class GetQuranVersesUseCase(private val repository: QuranRepository) {
    
    operator fun invoke(): Flow<List<Quran>> = repository.getAllQuranVerses()
    
    fun bySura(suraNumber: Int): Flow<List<Quran>> = repository.getVersesBySura(suraNumber)
    
    fun byPage(pageNumber: Int): Flow<List<Quran>> = repository.getVersesByPage(pageNumber)
}