package com.braveboy.mos_haf.data.local.database

import android.content.Context
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

object DatabaseHelper {
    private const val TAG = "DatabaseHelper"
    private const val DATABASE_NAME = "quran-text.db"
    
    /**
     * Copy database from assets to app's database directory
     */
    fun copyDatabaseFromAssets(context: Context): Boolean {
        return try {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            
            // If database already exists and has data, don't copy again
            if (dbFile.exists() && dbFile.length() > 0) {
                Log.d(TAG, "Database already exists at: ${dbFile.absolutePath}")
                Log.d(TAG, "Database size: ${dbFile.length()} bytes")
                return true
            }
            
            // Create parent directories if they don't exist
            dbFile.parentFile?.mkdirs()
            
            val assetPath = "database/quran-text.db"
            
            val inputStream = context.assets.open(assetPath)
            
            // Copy the file
            FileOutputStream(dbFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            inputStream.close()
            
            Log.d(TAG, "Database copied successfully to: ${dbFile.absolutePath}")
            Log.d(TAG, "Database size after copy: ${dbFile.length()} bytes")
            
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error copying database: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    /**
     * List all files in assets for debugging
     */
    fun listAssets(context: Context) {
        try {
            val assetManager = context.assets
            
            // List root
            val rootFiles = assetManager.list("")?.toList() ?: emptyList()
            Log.d(TAG, "Files in assets root: $rootFiles")
            
            // List databases folder if it exists
            if (rootFiles.contains("database")) {
                val dbFiles = assetManager.list("database")?.toList() ?: emptyList()
                Log.d(TAG, "Files in database folder: $dbFiles")
            }
            
            // List all subdirectories
            for (file in rootFiles) {
                if (!file.contains(".")) { // Probably a directory
                    try {
                        val subFiles = assetManager.list(file)?.toList() ?: emptyList()
                        Log.d(TAG, "Files in $file folder: $subFiles")
                    } catch (e: Exception) {
                        // Not a directory
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error listing assets: ${e.message}")
        }
    }
}