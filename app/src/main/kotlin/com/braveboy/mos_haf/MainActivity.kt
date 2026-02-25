package com.braveboy.mos_haf

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.di.appModule
import com.braveboy.mos_haf.presentation.navigation.ScreenNavController
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainActivity)
            modules(appModule)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("QuranApplication", "Initializing database...")
                val database = AppDatabase.getInstance(this@MainActivity)

                // Try to access database to force creation
                val quranCount = database.quranDao().getQuranCount()

                Log.d("QuranApplication", "Database initialized successfully")
                Log.d("QuranApplication", "Quran verses count: $quranCount")

            } catch (e: Exception) {
                Log.e("QuranApplication", "Error initializing database: ${e.message}")
                e.printStackTrace()
            }
        }

        enableEdgeToEdge()
        setContent {
            MoshafTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenNavController()
                }
            }
        }
    }
}
