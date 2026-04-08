package com.braveboy.mos_haf.presentation.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val PERSIAN_CHARACTERS = "^[\\s- ٔآابّپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیئءؤةأيك]+$".toRegex()

class SearchViewModel(
    savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.toRoute<Screen.Search>().let { detail ->
                if (detail.fromLast) {
                    getBookmark()
                    loadInitialData()
                } else {
                    loadInitialData()
                }
            }
        }
    }

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.LoadVersesByDetailedRange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    loadVersesByDetailedRange(
                        intent.startSura,
                        intent.startAya,
                        intent.endSura,
                        intent.endAya
                    )
                }
            }

            is SearchIntent.LoadVersesByJozAndHezb -> {
                loadVersesByJozAndHezb(
                    intent.joz,
                    intent.hezb
                )
            }

            is SearchIntent.LoadVersesBySura -> {
                loadVersesBySura(intent.sura)
            }

            is SearchIntent.LoadSuraNames -> loadInitialData()
            is SearchIntent.RefreshData -> loadInitialData()
            is SearchIntent.SaveBookmark -> saveBookmark(intent.lastRead)
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

            try {
                var verses =
                    getQuranVersesUseCase.byDetailedRange(startSura, startAya, endSura, endAya)
                val translates =
                    getQuranVersesUseCase.getByTranslateRange(startSura, startAya, endSura, endAya)

                if (verses.isNotEmpty()) {
                    val bismillahPattern = Regex(
                        "بِسْمِ\\s*اللَّهِ\\s*الرَّحْمَـٰنِ\\s*الرَّحِيمِ"
                    )

                    verses = verses.map { verse ->
                        verse.copy(
                            text = verse.text.replace(bismillahPattern, "").trim()
                        )
                    }
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        verses = verses,
                        translations = translates
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Error loading verse or translate") }
            }
        }

    }

    private fun loadVersesByJozAndHezb(
        joz: Int,
        hezb: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                var verses = getQuranVersesUseCase.byJozAndHezb(joz, (((joz - 1) * 4) + hezb))
                var translates = emptyList<String>()

                if (verses.isNotEmpty()) {
                    val bismillahPattern = Regex(
                        "بِسْمِ\\s*اللَّهِ\\s*الرَّحْمَـٰنِ\\s*الرَّحِيمِ"
                    )

                    verses = verses.map { verse ->
                        verse.copy(
                            text = verse.text.replace(bismillahPattern, "").trim()
                        )
                    }

                    translates = getQuranVersesUseCase.getByTranslateRange(
                        startSura = verses.first().sura,
                        startAya = verses.first().aya,
                        endSura = verses.last().sura,
                        endAya = verses.last().aya
                    )
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        verses = verses,
                        translations = translates
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Error loading verse or translate") }
            }
        }

    }

    private fun loadVersesBySura(sura: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            val suraName = state.value.suraNames[sura]
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

    fun saveBookmark(verses: LastReadModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookmark = Json.encodeToString(verses)
            preferencesRepository.saveSetting("bookmark", bookmark)
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
            _state.value.lastRead?.let {
                loadVersesByDetailedRange(
                    startSura = it.start?.sura ?: 0,
                    startAya = it.start?.aya ?: 0,
                    endSura = it.end?.sura ?: 0,
                    endAya = it.end?.aya ?: 0
                )
            }
        }
    }
}
