package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.QuranRepository
import com.braveboy.mos_haf.domain.model.Quran

class GetVerseByHezbUseCase(private val repository: QuranRepository) {
    fun getByHezb(hezb: Int): List<Quran> {
        return repository.getByHezb(hezb)
    }
}