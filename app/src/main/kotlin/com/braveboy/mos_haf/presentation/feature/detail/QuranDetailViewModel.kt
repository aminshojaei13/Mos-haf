package com.braveboy.mos_haf.presentation.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.navigation.Screen.QuranDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuranDetailViewModel (
    savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase
): ViewModel() {

    private var suraName = ""

    var loading = MutableStateFlow(false)
    private val _stateVerse = MutableStateFlow( emptyList<Quran>())
    val stateVerse: StateFlow<List<Quran>> = _stateVerse

    private val _stateTranslate = MutableStateFlow( emptyList<String>())
    val stateTranslate: StateFlow<List<String>> = _stateTranslate

    init {
        suraName = savedStateHandle.toRoute<QuranDetail>().sura
        viewModelScope.launch(Dispatchers.IO) {
            loadVersesBySura(suraName)
        }
    }

    private fun loadVersesBySura(suraName: String) {
        _stateVerse.update {
            getQuranVersesUseCase.bySuraName(suraName)
        }
        loadTranslateBySura()
    }

    private fun loadTranslateBySura() {
        _stateTranslate.update {
            getQuranVersesUseCase.getSuraTranslate(stateVerse.value.first().sura)
        }
        shouldShow()
    }

    fun shouldShow() {
        if (stateVerse.value.isNotEmpty() && stateTranslate.value.isNotEmpty()){
            loading.update {
                false
            }
        }
    }
}