package com.braveboy.mos_haf.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.braveboy.mos_haf.data.local.entity.KhatmEntity

@Dao
interface KhatmDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertKhatmQuran(khatm: KhatmEntity)

    @Query("SELECT * FROM khatm_quran ORDER BY id DESC")
    fun getAllKhatmQuran(): List<KhatmEntity>

    @Update
    fun updateKhatmQuran(khatm: KhatmEntity)

    @Delete
    fun deleteKhatmQuran(khatm: KhatmEntity)

    @Query("SELECT * FROM khatm_quran WHERE id = :id")
    fun getKhatmQuran(id: Int): KhatmEntity
}