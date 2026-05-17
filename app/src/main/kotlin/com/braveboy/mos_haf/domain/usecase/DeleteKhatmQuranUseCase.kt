package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.data.repository.KhatmRepository

class DeleteKhatmQuranUseCase(private val repository: KhatmRepository ) {
    fun invoke(khatm: KhatmEntity) = repository.deleteKhatmQuran(khatm)
}