package com.braveboy.mos_haf.presentation.feature.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.braveboy.mos_haf.AppConstants.BOOKMARK
import com.braveboy.mos_haf.AppConstants.FONT_SIZE
import com.braveboy.mos_haf.AppConstants.TRANSLATION_FONT_SIZE
import com.braveboy.mos_haf.AppConstants.THEME_TYPE
import com.braveboy.mos_haf.AppConstants.RECITER
import com.braveboy.mos_haf.AppConstants.VIEW_COUNT
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import com.braveboy.mos_haf.presentation.navigation.Screen.QuranDetail
import com.braveboy.mos_haf.presentation.feature.player.data.Reciter
import com.braveboy.mos_haf.ui.theme.ThemeType
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
        getTheme()
        getThemeType()
        getReciter()
        getFontSize()
        getTranslationFontSize()
        incrementViewCount()
        savedStateHandle.toRoute<QuranDetail>().let { detail ->
            if (detail.fromLast) {
                _state.update { it.copy(suraName = detail.sura) }
                getBookmark()
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
            saveThemeType(if (isDark) ThemeType.DARK else ThemeType.LIGHT)
        }
    }

    fun saveThemeType(themeType: ThemeType) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting(THEME_TYPE, themeType.name)
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

    fun getThemeType() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.readSettingAsFlow(THEME_TYPE).collect { themeName ->
                val type = try {
                    ThemeType.valueOf(themeName.orEmpty())
                } catch (e: Exception) {
                    if (state.value.isDarkMode) ThemeType.DARK else ThemeType.LIGHT
                }
                _state.update { it.copy(themeType = type) }
            }
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
            Log.d("xavi", "getFontSize: ${state.value.fontSize}")
        }
    }

    fun saveTranslationFontSize(fontSize: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting(TRANSLATION_FONT_SIZE, fontSize.toString())
        }
    }

    fun getTranslationFontSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val fontSize = preferencesRepository.readSetting(TRANSLATION_FONT_SIZE)
            _state.update { it.copy(translationFontSize = fontSize?.toFloatOrNull()) }
        }
    }

    private fun incrementViewCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = preferencesRepository.readSetting(VIEW_COUNT)?.toIntOrNull() ?: 0
            preferencesRepository.saveSetting(VIEW_COUNT, (count + 1).toString())
        }
    }
}
