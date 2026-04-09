package com.braveboy.mos_haf.presentation.feature.suralist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SuraListViewModel(
    private val getQuranVersesUseCase: GetQuranVersesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(listOf<Quran>())
    val state: StateFlow<List<Quran>> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllSura()
        }
    }

    private fun getAllSura() {
        getQuranVersesUseCase.getAllSuraWithDetail().let { suras ->
            _state.update { suras.distinctBy { it.suraName } }
        }
    }
}