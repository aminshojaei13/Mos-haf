package com.braveboy.mos_haf.data.local.database

import android.content.Context
import android.util.Log
import java.io.File

object DatabaseDiagnostic {
    
    fun runDiagnostic(context: Context) {
        Log.d("DatabaseDiagnostic", "========== DATABASE DIAGNOSTIC ==========")
        
        try {
            // 1. Check assets
            val assetManager = context.assets
            val assetFiles = assetManager.list("")?.toList() ?: emptyList()
            Log.d("DatabaseDiagnostic", "Assets root files: $assetFiles")
            
            // 2. Check databases folder
            if (assetFiles.contains("databases")) {
                val dbFiles = assetManager.list("databases")?.toList() ?: emptyList()
                Log.d("DatabaseDiagnostic", "Files in databases folder: $dbFiles")
            }
            
            // 3. Check app's database directory
            val dbDir = context.getDatabasePath("").parentFile
            Log.d("DatabaseDiagnostic", "Database directory: ${dbDir?.absolutePath}")
            
            if (dbDir?.exists() == true) {
                val dbFiles = dbDir.listFiles()?.map { "${it.name} (${it.length()} bytes)" }
                Log.d("DatabaseDiagnostic", "Existing database files: $dbFiles")
            }
            
            // 4. Try to open database file directly
            val dbFile = context.getDatabasePath("quran_database.db")
            Log.d("DatabaseDiagnostic", "Target database path: ${dbFile.absolutePath}")
            Log.d("DatabaseDiagnostic", "Database exists: ${dbFile.exists()}")
            
            if (dbFile.exists()) {
                Log.d("DatabaseDiagnostic", "Database size: ${dbFile.length()} bytes")
            }
            
        } catch (e: Exception) {
            Log.e("DatabaseDiagnostic", "Error in diagnostic: ${e.message}")
            e.printStackTrace()
        }
        
        Log.d("DatabaseDiagnostic", "========== END DIAGNOSTIC ==========")
    }
}