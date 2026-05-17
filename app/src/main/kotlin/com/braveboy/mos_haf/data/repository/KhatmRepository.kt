package com.braveboy.mos_haf.data.repository

import com.braveboy.mos_haf.data.local.datasource.LocalDataSource
import com.braveboy.mos_haf.data.local.entity.KhatmEntity

class KhatmRepository(private val localDataSource: LocalDataSource) {
    suspend fun insertKhatmQuran(khatm: KhatmEntity) {
        localDataSource.insertKhatmQuran(khatm)
    }

    fun updateKhatmQuran(khatm: KhatmEntity) {
        localDataSource.updateKhatmQuran(khatm)
    }

    fun getAllKhatmQuran(): List<KhatmEntity> =
        localDataSource.getAllKhatmQuran()

    fun getKhatmQuran(id: Int) =
        localDataSource.getKhatmQuran(id)

    fun deleteKhatmQuran(khatm: KhatmEntity) =
        localDataSource.deleteKhatmQuran(khatm)
}