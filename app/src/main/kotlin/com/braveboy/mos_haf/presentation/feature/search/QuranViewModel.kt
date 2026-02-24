package com.braveboy.mos_haf.presentation.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.domain.usecase.GetQuranVersesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class QuranViewModel(
    private val getQuranVersesUseCase: GetQuranVersesUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(QuranState())
    val state: StateFlow<QuranState> = _state
    
    fun handleIntent(intent: QuranIntent) {
        when (intent) {
            is QuranIntent.LoadAllVerses -> loadAllVerses()
            is QuranIntent.LoadVersesBySura -> loadVersesBySura(intent.suraNumber)
            is QuranIntent.LoadVersesByPage -> loadVersesByPage(intent.pageNumber)
            is QuranIntent.RefreshData -> refreshData()
        }
    }
    
    private fun loadAllVerses() {
        _state.update { it.copy(isLoading = true, error = null) }
        
        getQuranVersesUseCase()
            .onEach { verses ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        verses = verses,
                        totalVerses = verses.size
                    )
                }
            }
            .catch { exception ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadVersesBySura(suraNumber: Int) {
        _state.update { it.copy(isLoading = true, error = null) }
        
        getQuranVersesUseCase.bySuraId(suraNumber)
            .onEach { verses ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        verses = verses
                    )
                }
            }
            .catch { exception ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Error loading sura verses"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun loadVersesByPage(pageNumber: Int) {
        _state.update { it.copy(isLoading = true, error = null, currentPage = pageNumber) }
        
        getQuranVersesUseCase.byPage(pageNumber)
            .onEach { verses ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        verses = verses
                    )
                }
            }
            .catch { exception ->
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Error loading page verses"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun refreshData() {
        loadAllVerses()
    }
}