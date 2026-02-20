package com.braveboy.mos_haf.data.local.database

import android.content.Context
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

object DatabaseHelper {
    private const val TAG = "DatabaseHelper"
    private const val DATABASE_NAME = "quran_database.db"
    
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
            
            // Try multiple possible asset paths
            val assetPaths = listOf(
                "databases/quran.db",
                "quran.db",
                "database/quran.db",
                "db/quran.db"
            )
            
            var inputStream: java.io.InputStream? = null
            var selectedPath: String? = null

            // Find which path exists
            for (path in assetPaths) {
                try {
                    inputStream = context.assets.open(path)
                    selectedPath = path
                    Log.d(TAG, "Found database at assets path: $path")
                    break
                } catch (e: IOException) {
                    // Path doesn't exist, try next
                }
            }
            
            if (inputStream == null) {
                // List all files in assets to help debug
                val assetFiles = context.assets.list("")?.joinToString() ?: "No files"
                val databaseFiles = context.assets.list("databases")?.joinToString() ?: "No database folder"
                
                Log.e(TAG, "Could not find database file in assets")
                Log.e(TAG, "Files in root assets: $assetFiles")
                Log.e(TAG, "Files in databases folder: $databaseFiles")
                
                return false
            }
            
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
            if (rootFiles.contains("databases")) {
                val dbFiles = assetManager.list("databases")?.toList() ?: emptyList()
                Log.d(TAG, "Files in databases folder: $dbFiles")
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