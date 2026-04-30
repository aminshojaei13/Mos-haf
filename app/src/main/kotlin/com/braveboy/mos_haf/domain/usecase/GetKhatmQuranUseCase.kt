package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.KhatmRepository

class GetKhatmQuranUseCase(private val repository: KhatmRepository) {
    fun getKhatmById(id: Int) = repository.getKhatmQuran(id)
}