package com.braveboy.mos_haf.data.local.database

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class DatabaseManager(private val context: Context) {

    private val tag = "DatabaseManager"

    /**
     * Check if database exists and has data
     */
    fun isDatabaseReady(): Boolean {
        return try {
            val dbFile = context.getDatabasePath("quran_database.db")
            if (!dbFile.exists()) {
                Log.d(tag, "Database file doesn't exist")
                return false
            }

            if (dbFile.length() == 0L) {
                Log.d(tag, "Database file is empty")
                return false
            }

            // Try to query count
            val database = AppDatabase.getInstance(context)
            val count = database.quranDao().getQuranCount()

            Log.d(tag, "Database ready with $count records")
            count > 0

        } catch (e: Exception) {
            Log.e(tag, "Error checking database: ${e.message}")
            false
        }
    }

    /**
     * Initialize database with background work
     */
    fun initializeDatabase(): Flow<DatabaseInitState> = flow {
        emit(DatabaseInitState.Loading)

        try {
            if (isDatabaseReady()) {
                Log.d(tag, "Database already ready")
                emit(DatabaseInitState.Success)
                return@flow
            }

            // Use WorkManager for background initialization
            val workRequest = OneTimeWorkRequestBuilder<DatabaseWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    1,
                    TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "database_init",
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )

            // Observe result
            WorkManager.getInstance(context)
                .getWorkInfoByIdFlow(workRequest.id)
                .collect { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            if (isDatabaseReady()) {
                                emit(DatabaseInitState.Success)
                            } else {
                                emit(DatabaseInitState.Error("Database initialized but empty"))
                            }
                        }

                        WorkInfo.State.FAILED -> {
                            emit(DatabaseInitState.Error("Database initialization failed"))
                        }

                        else -> {
                            // Still loading
                        }
                    }
                }

        } catch (e: Exception) {
            Log.e(tag, "Error in database initialization: ${e.message}")
            emit(DatabaseInitState.Error(e.message ?: "Unknown error"))
        }
    }

    sealed class DatabaseInitState {
        object Loading : DatabaseInitState()
        object Success : DatabaseInitState()
        data class Error(val message: String) : DatabaseInitState()
    }
}