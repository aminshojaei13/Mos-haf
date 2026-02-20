package com.braveboy.mos_haf.data.local.database

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DatabaseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            Log.d("DatabaseWorker", "Starting database initialization work")
            
            // Copy database
            val success = DatabaseHelper.copyDatabaseFromAssets(applicationContext)
            
            if (success) {
                // Verify database
                val database = AppDatabase.getInstance(applicationContext)
                val quranCount = database.quranDao().getQuranCount()

                Log.d("DatabaseWorker", "Database initialized - Quran: $quranCount")
                
                if (quranCount > 0) {
                    Result.success()
                } else {
                    Result.retry()
                }
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("DatabaseWorker", "Error in database worker: ${e.message}")
            Result.failure()
        }
    }
}