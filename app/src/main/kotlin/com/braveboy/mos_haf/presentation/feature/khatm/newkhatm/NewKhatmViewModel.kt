package com.braveboy.mos_haf.presentation.feature.khatm.newkhatm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.domain.usecase.InsertKhatmQuranUseCase
import com.braveboy.mos_haf.presentation.feature.search.PERSIAN_CHARACTERS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewKhatmViewModel(
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val insertKhatmQuranUseCase: InsertKhatmQuranUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NewKhatmState())
    val state: StateFlow<NewKhatmState> = _state

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val suraNames = getQuranVersesUseCase.getAllSura()
                val ayaCounts = getQuranVersesUseCase.getAyaCounts()
                _state.update { quranState ->
                    suraNames.forEach { sura ->
                        sura.filter {
                            PERSIAN_CHARACTERS.matches(it.toString())
                        }
                    }
                    quranState.copy(
                        suraNames = suraNames,
                        ayaCounts = ayaCounts
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Error loading suras") }
            }
        }
    }

    fun createNewKhatm(khatm: KhatmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            insertKhatmQuranUseCase.insertKhatmQuran(khatm)
        }
    }

}