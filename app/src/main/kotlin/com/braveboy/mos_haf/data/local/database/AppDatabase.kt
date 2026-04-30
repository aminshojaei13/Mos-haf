package com.braveboy.mos_haf.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.braveboy.mos_haf.data.local.dao.KhatmDao
import com.braveboy.mos_haf.data.local.dao.QuranDao
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.data.local.entity.QuranCleanTextEntity
import com.braveboy.mos_haf.data.local.entity.QuranEntity
import com.braveboy.mos_haf.data.local.entity.QuranTranslateEntity

@Database(
    entities = [
        QuranEntity::class,
        QuranCleanTextEntity::class,
        QuranTranslateEntity::class,
        KhatmEntity::class
    ],
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quranDao(): QuranDao
    abstract fun khatmDao(): KhatmDao

    companion object {
        private const val DATABASE_NAME = "quran-text.db"
        private const val TAG = "AppDatabase"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            Log.d(TAG, "getInstance called")

            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "Creating new database instance")

                // First, ensure database file is copied from assets
                val copySuccess = DatabaseHelper.copyDatabaseFromAssets(context)
                Log.d(TAG, "Database copy success: $copySuccess")

                // List assets for debugging
                DatabaseHelper.listAssets(context)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).setQueryExecutor { sql ->
                    Log.d(TAG, "Executing query: $sql")
                }
                    .fallbackToDestructiveMigration(false)
                    .build()

                // Verify database has data
                try {
                    instance.quranDao().getQuranCount()
                } catch (e: Exception) {
                    Log.e(TAG, "Error verifying database: ${e.message}")
                }

                INSTANCE = instance
                instance
            }
        }
    }
}