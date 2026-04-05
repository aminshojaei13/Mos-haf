package com.braveboy.mos_haf.presentation.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.navigation.Screen.QuranDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuranDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuranDetailState())
    val state: StateFlow<QuranDetailState> = _state.asStateFlow()

    init {
        val suraName: String = savedStateHandle.toRoute<QuranDetail>().sura
        _state.update { it.copy(suraName = suraName) }
        loadVersesAndTranslations(suraName)
        getAllSura()
    }

    private fun getAllSura() {
        viewModelScope.launch(Dispatchers.IO) {
            getQuranVersesUseCase.getAllSura().let { suras ->
                _state.update { it.copy(suraNames = suras) }
            }
        }
    }

    fun loadVersesAndTranslations(suraName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            var verses = getQuranVersesUseCase.bySuraName(suraName)
            val translations = if (verses.isNotEmpty()) {
                getQuranVersesUseCase.getSuraTranslate(verses.first().sura)
            } else {
                emptyList()
            }

            if (verses.isNotEmpty()) {
                val firstVerse = verses.first()

                val bismillahPattern = Regex(
                    "بِسْمِ\\s*اللَّهِ\\s*الرَّحْمَـٰنِ\\s*الرَّحِيمِ"
                )

                val modifiedText = firstVerse.text.replace(bismillahPattern, "").trim()

                val modifiedFirstVerse = firstVerse.copy(text = modifiedText)
                verses = verses.toMutableList().apply { set(0, modifiedFirstVerse) }

            }

            _state.update {
                it.copy(
                    verses = verses,
                    translations = translations,
                    isLoading = false
                )
            }
        }
    }
}
