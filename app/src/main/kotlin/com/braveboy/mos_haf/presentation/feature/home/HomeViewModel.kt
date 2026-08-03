package com.braveboy.mos_haf.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.domain.model.LastReadModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LastReadModel(null, null,null, null))
    val state: StateFlow<LastReadModel> = _state.asStateFlow()

    private val _theme = MutableStateFlow(false)
    val theme: StateFlow<Boolean> = _theme.asStateFlow()

    private val _saveAudio = MutableStateFlow<Boolean?>(null)
    val saveAudio: StateFlow<Boolean?> = _saveAudio.asStateFlow()

    init {
        getTheme()
        getSaveAudio()
    }

    fun getTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.readSettingAsFlow("theme").collect { theme ->
                _theme.value = theme.toBoolean()
            }
        }
    }

    fun getSaveAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.readSettingAsFlow("save_audio").collect { save ->
                _saveAudio.value = save?.toBoolean()
            }
        }
    }

    fun saveAudioSetting(save: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting("save_audio", save.toString())
        }
    }

    fun saveTheme(isDark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.saveSetting("theme", isDark.toString())
        }
    }

    fun getLastRead() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.readSetting("bookmark")?.let { bookmark ->
                val last = Json.decodeFromString<LastReadModel>(bookmark)
                _state.update { it.copy(id = last.id, source = last.source, start = last.start, end = last.end) }
            }
        }
    }
}