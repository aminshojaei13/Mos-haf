package com.braveboy.mos_haf.domain.usecase

import com.braveboy.mos_haf.data.repository.QuranRepository

class GetVerseByPageUseCase(private val repository: QuranRepository) {
    fun getByPage(page: Int) = repository.getByPage(page)

}