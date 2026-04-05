package com.braveboy.mos_haf.presentation.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val PERSIAN_CHARACTERS = "^[\\s- ٔآابّپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیئءؤةأيك]+$".toRegex()

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

            is KhatmQuranIntent.LoadVersesByJozAndHezb -> {
                loadVersesByJozAndHezb(
                    intent.joz,
                    intent.hezb
                )
            }

            is KhatmQuranIntent.LoadVersesBySura -> {
                loadVersesBySura(intent.sura)
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
                var verses = getQuranVersesUseCase.byJozAndHezb(joz, (((joz-1) * 4) + hezb))
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

    private fun loadVersesBySura(sura:Int) {
        viewModelScope.launch(Dispatchers.IO){
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
}
