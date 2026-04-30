package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.KhatmRepository

class GetAllKhatmQuranUseCase(private val repository: KhatmRepository) {
     fun getAllKhatm() = repository.getAllKhatmQuran()
}