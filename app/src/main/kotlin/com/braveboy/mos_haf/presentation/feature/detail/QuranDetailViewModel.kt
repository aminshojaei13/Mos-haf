package com.braveboy.mos_haf.presentation.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.navigation.Screen.QuranDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class QuranDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuranDetailState())
    val state: StateFlow<QuranDetailState> = _state.asStateFlow()

    init {
        savedStateHandle.toRoute<QuranDetail>().let { detail ->
            if (detail.fromLast) {
                _state.update { it.copy(suraName = detail.sura) }
                getBookmark()
                viewModelScope.launch(Dispatchers.IO) {
                    state.value.lastRead?.let {
                        if (it.start != null) {
                            loadVersesAndTranslations(
                                it.start.suraName.orEmpty()
                            )
                        }
                    }
                }
            } else {
                _state.update { it.copy(suraName = detail.sura) }
                loadVersesAndTranslations(detail.sura)
            }
        }
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

    private fun getBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            val detail = preferencesRepository.readSetting("bookmark")
            Json.decodeFromString<LastReadModel>(detail.orEmpty()).let {
                _state.update { quranDetailState ->
                    quranDetailState.copy(lastRead = it)
                }
            }
        }
    }

    fun saveBookmark(verses: LastReadModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookmark = Json.encodeToString(verses)
            preferencesRepository.saveSetting("bookmark", bookmark)
        }
    }
}
