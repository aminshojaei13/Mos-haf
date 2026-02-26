package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.QuranRepository
import com.braveboy.mos_haf.domain.model.Quran
import kotlinx.coroutines.flow.Flow

class GetQuranVersesUseCase(private val repository: QuranRepository) {

    operator fun invoke(): Flow<List<Quran>> = repository.getAllQuranVerses()

    fun bySuraId(suraNumber: Int): Flow<List<Quran>> = repository.getVersesBySura(suraNumber)

    fun bySuraName(suraName: String): List<Quran> = repository.getVersesBySura(suraName)

    fun byPage(pageNumber: Int): Flow<List<Quran>> = repository.getVersesByPage(pageNumber)

    fun byDetailedRange(startSura: Int, startAya: Int, endSura: Int, endAya: Int): List<Quran> =
        repository.getVersesByDetailedRange(startSura, startAya, endSura, endAya)

    fun getAllSura(): List<String> = repository.getAllSura()

    fun getAyaCounts(): List<Int> = repository.getAyaCounts()

    fun getSuraTranslate(suraNumber: Int): List<String> = repository.getSuraTranslate(suraNumber)

    fun getByTranslateRange(startSura: Int, startAya: Int, endSura: Int, endAya: Int): List<String> =
        repository.getByTranslateRange(startSura,startAya,endSura,endAya)
}
