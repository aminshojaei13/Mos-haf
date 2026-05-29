package com.braveboy.mos_haf.data.repository

import android.content.Context
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.data.local.HezbTime
import com.braveboy.mos_haf.data.local.HezbTimingData
import com.google.gson.Gson

class HezbTimeRepository(private val context: Context) {

    private lateinit var hezbTimes: List<HezbTime>

    init {
        loadHezbTimesFromJson()
    }

    private fun loadHezbTimesFromJson() {
        try {
            val jsonString = context.resources.openRawResource(R.raw.hezbtimes)
                .bufferedReader().use { it.readText() }

            val gson = Gson()
            val data = gson.fromJson(jsonString, HezbTimingData::class.java)
            hezbTimes = data.hezbs
        } catch (e: Exception) {
            e.printStackTrace()
            hezbTimes = emptyList()
        }
    }

    // دریافت زمان شروع و پایان به میلی‌ثانیه
    fun getHezbStartAndEndInMillis(globalHezbIndex: Int): Pair<Long, Long> {
        require(globalHezbIndex in 1..hezbTimes.size) {
            "حزب باید بین 1 تا ${hezbTimes.size} باشد"
        }

        val endTimeInSeconds = hezbTimes[globalHezbIndex - 1].endSeconds
        val startTimeInSeconds = if (globalHezbIndex > 1) {
            hezbTimes[globalHezbIndex - 2].endSeconds
        } else {
            0.0
        }

        return (startTimeInSeconds.toMillis()) to (endTimeInSeconds.toMillis())
    }

    fun Double.toMillis(): Long {
        val minutes = this.toInt()
        val seconds = ((this - minutes) * 100).toInt()

        return (minutes * 60 * 1000L) + (seconds * 1000L)
    }

    // دریافت تمام زمان‌های حزب‌ها به میلی‌ثانیه
    fun getAllHezbTimesInMillis(): List<Pair<Int, Long>> {
        return hezbTimes.mapIndexed { index, hezbTime ->
            (index + 1) to (hezbTime.endSeconds * 1000).toLong()
        }
    }

    // دریافت محدوده زمانی یک جزء کامل به میلی‌ثانیه
    fun getPartTimeRangeInMillis(partNumber: Int): Pair<Long, Long> {
        val startHezb = (partNumber - 1) * 2 + 1
        val endHezb = minOf(partNumber * 2, hezbTimes.size)

        val startTimeInMillis = if (startHezb == 1) 0L
        else (hezbTimes[startHezb - 2].endSeconds * 1000).toLong()
        val endTimeInMillis = (hezbTimes[endHezb - 1].endSeconds * 1000).toLong()

        return startTimeInMillis to endTimeInMillis
    }

    // دریافت زمان شروع یک حزب خاص به میلی‌ثانیه
    fun getHezbStartTimeInMillis(globalHezbIndex: Int): Long {
        return if (globalHezbIndex == 1) 0L
        else (hezbTimes[globalHezbIndex - 2].endSeconds * 1000).toLong()
    }

    // دریافت زمان پایان یک حزب خاص به میلی‌ثانیه
    fun getHezbEndTimeInMillis(globalHezbIndex: Int): Long {
        return (hezbTimes[globalHezbIndex - 1].endSeconds * 1000).toLong()
    }
}