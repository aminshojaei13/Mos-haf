package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.QuranRepository
import com.braveboy.mos_haf.domain.model.Quran

class GetVerseByJozUseCase(private val repository: QuranRepository) {
    fun getByJoz(joz: Int):List<Quran> {
        return repository.getByJoz(joz)
    }
}