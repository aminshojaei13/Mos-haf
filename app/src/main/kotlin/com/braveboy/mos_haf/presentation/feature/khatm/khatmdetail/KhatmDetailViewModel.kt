package com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail

import android.util.Log
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
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.toRoute<Screen.KhatmDetail>().let { detail ->
                _state.update { it.copy(khatmId = detail.khatmId) }
            }
            loadInitialData()
            //getKhatmDetail()
        }
    }

    fun handleIntent(intent: KhatmDetailIntent) {
        when (intent) {
            KhatmDetailIntent.LoadKhatmDetail -> getKhatmDetail()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val suras = getQuranVersesUseCase.getAllSuraWithDetail()
                val ayaCounts = getQuranVersesUseCase.getAyaCounts()
                _state.update { quranState ->
                    suras.forEach { sura ->
                        sura.suraName?.filter {
                            PERSIAN_CHARACTERS.matches(it.toString())
                        }
                    }
                    quranState.copy(
                        qurans = suras,
                        ayaCounts = ayaCounts
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Error loading suras") }
            }
        }
    }

    private fun getKhatmDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            val khatm = state.value.khatmId?.let { getKhatmQuranUseCase.getKhatmById(it) }
            Log.d("xavi", "KhatmDetailScreen: $khatm")
            _state.update { it.copy(khatmDetail = khatm) }

        }
    }
}