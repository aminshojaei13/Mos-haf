package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.data.repository.KhatmRepository

class InsertKhatmQuranUseCase(private val repository: KhatmRepository) {
    suspend fun insertKhatmQuran(khatm: KhatmEntity)  = repository.insertKhatmQuran(khatm)
}