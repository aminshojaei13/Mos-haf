package com.braveboy.mos_haf.presentation.feature.khatm.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.domain.usecase.GetAllKhatmQuranUseCase
import com.braveboy.mos_haf.domain.usecase.InsertKhatmQuranUseCase
import com.braveboy.mos_haf.presentation.feature.khatm.newkhatm.KhatmType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KhatmHomeViewModel(
    private val insertKhatmQuranUseCase: InsertKhatmQuranUseCase,
    private val getAllKhatmQuranUseCase: GetAllKhatmQuranUseCase
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
                        pagesPerDay = when {
                            intent.type == KhatmType.RANGE.name -> 2
                            intent.type == KhatmType.PAGE.name -> 1
                            else -> 0
                        },
                        completedPages = 0,
                        startDate = System.currentTimeMillis(),
                        lastReadDate = null,
                        isActive = true
                    )
                )
            }
        }
    }

    init {
        getAllKhatm()
    }

    private fun createNewKhatm(khatmEntity: KhatmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            insertKhatmQuranUseCase.insertKhatmQuran(khatmEntity)
            getAllKhatm()
        }
    }

    private fun getAllKhatm() {
        viewModelScope.launch(Dispatchers.IO) {
            //_state.update { it.copy(khatms = a) }
            Log.d("xavi", "getAllKhatmmmmm: ww")
            val list = getAllKhatmQuranUseCase.getAllKhatm()
            Log.d("xavi", "getAllKhatmmmmm: $list")
            _state.update { it.copy(khatms = list) }
        }
    }

}