package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.data.repository.KhatmRepository

class UpdateKhatmQuranUseCase(private val repository: KhatmRepository) {
    fun updateKhatmQuran(khatm: KhatmEntity) = repository.updateKhatmQuran(khatm)
}