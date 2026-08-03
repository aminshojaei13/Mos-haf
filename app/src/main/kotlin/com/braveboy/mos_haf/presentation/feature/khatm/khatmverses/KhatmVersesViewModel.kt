package com.braveboy.mos_haf.presentation.feature.khatm.khatmverses

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.AppConstants.BOOKMARK
import com.braveboy.mos_haf.AppConstants.FONT_SIZE
import com.braveboy.mos_haf.AppConstants.RECITER
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.usecase.GetKhatmQuranUseCase
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.domain.usecase.GetVerseByHezbUseCase
import com.braveboy.mos_haf.domain.usecase.GetVerseByJozUseCase
import com.braveboy.mos_haf.domain.usecase.GetVerseByPageUseCase
import com.braveboy.mos_haf.domain.usecase.UpdateKhatmQuranUseCase
import com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail.KhatmType
import com.braveboy.mos_haf.presentation.feature.khatm.model.KhatmVersesModel
import com.braveboy.mos_haf.presentation.feature.player.data.Reciter
import com.braveboy.mos_haf.presentation.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KhatmVersesViewModel(
    savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val getVerseByPageUseCase: GetVerseByPageUseCase,
    private val getVerseByJozUseCase: GetVerseByJozUseCase,
    private val getVerseByHezbUseCase: GetVerseByHezbUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val updateKhatmQuranUseCase: UpdateKhatmQuranUseCase,
    private val getKhatmQuranUseCase: GetKhatmQuranUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(KhatmVersesState())
    val state: StateFlow<KhatmVersesState> = _state.asStateFlow()

    init {
        getTheme()
        getFontSize()
        getReciter()
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.toRoute<Screen.KhatmVerses>().let { detail ->
                Json.decodeFromString<KhatmVersesModel>(detail.khatm).let { detailKhatm ->
                    if (detail.fromLast) {
                        getBookmark()
                        loadVerseByType(detailKhatm)
                    } else {
                        _state.update { it.copy(khatm = detailKhatm) }
                        loadVerseByType(detailKhatm)
                    }
                }
            }
            state.value.khatm?.id?.let { khatmId ->
                val khatm = getKhatmQuranUseCase.getKhatmById(khatmId)
                _state.update { it.copy(khatmDetail = khatm) }
            }
        }
    }

    fun handleIntent(intent: KhatmVersesIntent) {
        when (intent) {
            is KhatmVersesIntent.LoadAnotherVerse -> {
                when (state.value.khatm?.type) {
                    KhatmType.JOZ.name -> {
                        loadVerseByType(
                            KhatmVersesModel(
                                type = KhatmType.JOZ.name,
                                joz = intent.id
                            )
                        )
                    }

                    KhatmType.HEZB.name -> {
                        loadVerseByType(
                            KhatmVersesModel(
                                type = KhatmType.HEZB.name,
                                hezb = intent.id
                            )
                        )
                    }

                    KhatmType.PAGE.name -> {
                        loadVerseByType(
                            KhatmVersesModel(
                                type = KhatmType.PAGE.name,
                                page = intent.id
                            )
                        )
                    }
                }
            }

            KhatmVersesIntent.SaveCompleteReadPage -> {
                updateReadPageKhatm()
                saveBookmark(
                    LastReadModel(
                        id = null,
                        source = null,
                        start = null,
                        end = null
                    )
                )
            }
        }
    }

    private fun loadVerseByType(khatmVersesModel: KhatmVersesModel) {
        khatmVersesModel.let { khatmDetail ->
            _state.update { it.copy(khatm = khatmDetail) }
            when (state.value.khatm?.type) {
                KhatmType.JOZ.name -> {
                    loadVersesByJoz(khatmDetail.joz ?: 0)
                }

                KhatmType.HEZB.name -> {
                    loadVersesByHezb(
                        hezb = khatmDetail.hezb ?: 0
                    )
                }

                KhatmType.PAGE.name -> {
                    loadVersesByPage(khatmDetail.page ?: 0)
                }
            }
        }
    }

    private fun loadVersesByHezb(
        hezb: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            try {
                var verses = getVerseByHezbUseCase.getByHezb(hezb)
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
                Log.e("Error", "${e.message} - Error loading verse or translate")
            }
        }

    }

    private fun loadVersesByJoz(
        joz: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            try {
                var verses = getVerseByJozUseCase.getByJoz(joz)
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
                Log.e("Error", "${e.message} - Error loading verse or translate")
            }
        }

    }

    private fun loadVersesByPage(
        page: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            try {
                var verses = getVerseByPageUseCase.getByPage(page)
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
                Log.e("Error", "${e.message} - Error loading verse or translate")
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

    private fun updateReadPageKhatm() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.khatmDetail?.let {
                updateKhatmQuranUseCase.updateKhatmQuran(
                    KhatmEntity(
                        id = it.id,
                        name = it.name,
                        type = it.type,
                        completedPages = if (
                            state.value.khatmDetail?.completedPages != null &&
                            state.value.khatmDetail?.completedPages!! > state.value.verses.last().page
                        ) {
                            state.value.khatmDetail?.completedPages!!
                        } else {
                            state.value.verses.last().page
                        },
                        startDate = it.startDate,
                        lastReadDate = System.currentTimeMillis(),
                        isActive = state.value.verses.last().page != 604
                    )
                )
            }
        }
    }

    private fun getBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            val detail = preferencesRepository.readSetting("bookmark")
            Json.decodeFromString<LastReadModel>(detail.orEmpty()).let { readModel ->
                _state.update { quranDetailState ->
                    quranDetailState.copy(lastRead = readModel)
                }
                state.value.lastRead?.let {
                    if (it.start != null) {
                        loadVersesAndTranslations(
                            it.start.suraName.orEmpty()
                        )
                    }
                }
            }
        }
    }

    fun saveBookmark(verses: LastReadModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookmark = Json.encodeToString(verses)
            preferencesRepository.saveSetting(BOOKMARK, bookmark)
        }
    }

    fun getTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.readSettingAsFlow("theme").collect { theme ->
                _state.update { it.copy(isDarkMode = theme.toBoolean()) }
            }
        }
    }

    fun saveTheme(isDark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting("theme", isDark.toString())
        }
    }

    fun saveFontSize(fontSize: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting(FONT_SIZE, fontSize.toString())
        }
    }

    fun getFontSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val fontSize = preferencesRepository.readSetting(FONT_SIZE)
            _state.update { it.copy(fontSize = fontSize?.toFloatOrNull()) }
        }
    }

    fun saveReciter(reciter: Reciter) {
        _state.update { it.copy(selectedReciter = reciter) }
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting(RECITER, reciter.name)
        }
    }

    fun getReciter() {
        viewModelScope.launch(Dispatchers.IO) {
            val reciterName = preferencesRepository.readSetting(RECITER)
            val reciter = try {
                Reciter.valueOf(reciterName.orEmpty())
            } catch (e: Exception) {
                Reciter.ALAFASY
            }
            _state.update { it.copy(selectedReciter = reciter) }
        }
    }
}
