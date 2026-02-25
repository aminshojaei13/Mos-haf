package com.braveboy.mos_haf.presentation.feature.suralist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SuraListViewModel(
    private val getQuranVersesUseCase: GetQuranVersesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(listOf<String>())
    val state: StateFlow<List<String>> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllSura()
        }
    }

    private fun getAllSura() {
        getQuranVersesUseCase.getAllSura().let { verses ->
            Log.d("xavi", "vie: $verses")
            _state.update { verses }
            Log.d("xavi", "vie sta: ${state.value}")

        }
            /*.onEach { verses ->
                _state.update {
                    verses
                }
            }
            .catch { exception ->
                *//*_state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Error loading sura verses"
                    )
                }*//*
            }
            .launchIn(viewModelScope)*/
    }
}