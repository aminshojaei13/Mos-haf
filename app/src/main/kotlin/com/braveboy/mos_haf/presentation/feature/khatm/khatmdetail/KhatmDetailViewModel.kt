package com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.domain.usecase.GetKhatmQuranUseCase
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.feature.search.PERSIAN_CHARACTERS
import com.braveboy.mos_haf.presentation.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KhatmDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val getKhatmQuranUseCase: GetKhatmQuranUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(KhatmDetailState())
    val state: StateFlow<KhatmDetailState> = _state

    init {
        savedStateHandle.toRoute<Screen.KhatmDetail>().let { detail ->
            _state.update { it.copy(khatmId = detail.khatmId) }
            viewModelScope.launch(Dispatchers.IO) {
                val khatm = getKhatmQuranUseCase.getKhatmById(detail.khatmId)
                _state.update { it.copy(khatmDetail = khatm) }
            }
        }
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
}