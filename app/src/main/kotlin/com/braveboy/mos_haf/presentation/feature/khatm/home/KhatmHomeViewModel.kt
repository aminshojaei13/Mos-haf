package com.braveboy.mos_haf.presentation.feature.khatm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.domain.usecase.DeleteKhatmQuranUseCase
import com.braveboy.mos_haf.domain.usecase.GetAllKhatmQuranUseCase
import com.braveboy.mos_haf.domain.usecase.InsertKhatmQuranUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KhatmHomeViewModel(
    private val insertKhatmQuranUseCase: InsertKhatmQuranUseCase,
    private val getAllKhatmQuranUseCase: GetAllKhatmQuranUseCase,
    private val deleteKhatmQuranUseCase: DeleteKhatmQuranUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(KhatmHomeState())
    val state: StateFlow<KhatmHomeState> = _state

    fun handleIntent(intent: KhatmHomeIntent) {
        when (intent) {
            is KhatmHomeIntent.NewKhatm -> {
                createNewKhatm(
                    KhatmEntity(
                        name = intent.name,
                        type = intent.type,
                        completedPages = 0,
                        startDate = System.currentTimeMillis(),
                        lastReadDate = null,
                        isActive = true
                    )
                )
            }

            KhatmHomeIntent.LoadKhatmDetail -> getAllKhatm()
            is KhatmHomeIntent.DeleteKhatm -> {
                deleteKhatmQuran(intent.khatmEntity)
            }
        }
    }

    private fun createNewKhatm(khatmEntity: KhatmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            insertKhatmQuranUseCase.insertKhatmQuran(khatmEntity)
            getAllKhatm()
        }
    }

    private fun getAllKhatm() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getAllKhatmQuranUseCase.getAllKhatm()
            _state.update { it.copy(khatms = list) }
        }
    }

    fun deleteKhatmQuran(khatm: KhatmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteKhatmQuranUseCase.invoke(khatm)
            getAllKhatm()
        }
    }

}