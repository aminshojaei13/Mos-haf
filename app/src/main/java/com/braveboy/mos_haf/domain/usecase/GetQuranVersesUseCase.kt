package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.QuranRepository
import com.braveboy.mos_haf.domain.model.Quran
import kotlinx.coroutines.flow.Flow

class GetQuranVersesUseCase(private val repository: QuranRepository) {

    operator fun invoke(): Flow<List<Quran>> = repository.getAllQuranVerses()

    fun bySura(suraNumber: Int): Flow<List<Quran>> = repository.getVersesBySura(suraNumber)

    fun byPage(pageNumber: Int): Flow<List<Quran>> = repository.getVersesByPage(pageNumber)
}