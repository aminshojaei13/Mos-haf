package com.braveboy.mos_haf

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.di.appModule
import com.braveboy.mos_haf.presentation.navigation.ScreenNavController
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        try {
            startKoin {
                androidLogger(Level.ERROR)
                androidContext(this@MainActivity)
                modules(appModule)
            }
        } catch (e: Exception) {
            Log.d("xavi", "onCreate: $e")
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
            var isDark by remember { mutableStateOf(false) }
            val pref = koinInject<PreferencesRepository>()
            lifecycleScope.launch(Dispatchers.IO) {
                pref.readSettingAsFlow("theme").collect {
                    Log.d("toni", "onCreate: $it")
                    isDark = it.toBoolean()
                }
            }

            MoshafTheme(
                darkTheme = isDark
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenNavController()
                }
            }
        }
    }
}
