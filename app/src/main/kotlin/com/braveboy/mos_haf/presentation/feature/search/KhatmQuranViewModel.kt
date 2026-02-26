package com.braveboy.mos_haf.presentation.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KhatmQuranViewModel(
    private val getQuranVersesUseCase: GetQuranVersesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(KhatmQuranState())
    val state: StateFlow<KhatmQuranState> = _state

    init {
        loadInitialData()
    }

    fun handleIntent(intent: KhatmQuranIntent) {
        when (intent) {
            is KhatmQuranIntent.LoadVersesByDetailedRange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    loadVersesByDetailedRange(
                        intent.startSura,
                        intent.startAya,
                        intent.endSura,
                        intent.endAya
                    )
                }
            }

            is KhatmQuranIntent.LoadSuraNames -> loadInitialData()
            is KhatmQuranIntent.RefreshData -> loadInitialData()
        }
    }

    private fun loadVersesByDetailedRange(
        startSura: Int,
        startAya: Int,
        endSura: Int,
        endAya: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true, error = null) }

            getQuranVersesUseCase.byDetailedRange(startSura, startAya, endSura, endAya)
                .let { verses ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            verses = verses
                        )
                    }
                }
        }

    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val suraNames = getQuranVersesUseCase.getAllSura()
                val ayaCounts = getQuranVersesUseCase.getAyaCounts()
                _state.update {
                    it.copy(
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
