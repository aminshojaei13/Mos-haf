package com.braveboy.mos_haf

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.util.UnstableApi
import com.braveboy.mos_haf.data.local.database.AppDatabase
import com.braveboy.mos_haf.data.repository.PreferencesRepository
import com.braveboy.mos_haf.di.appModule
import com.braveboy.mos_haf.presentation.feature.player.data.AudioCache
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
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        AudioCache.release()
        File(cacheDir, "audio_cache").deleteRecursively()
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()

        try {
            val audioCache = File(this.applicationContext.cacheDir, "audio_cache")
            if (audioCache.exists()) {
                // Last modified time
                val lastModified = audioCache.lastModified()
                // 24 hours in milliseconds
                val expiryTime = TimeUnit.HOURS.toMillis(24)
                val isExpired = System.currentTimeMillis() - lastModified > expiryTime
                if (isExpired) {
                    if (audioCache.isDirectory) {
                        audioCache.deleteRecursively()
                    } else {
                        audioCache.delete()
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("xavi", "onCreate: $e")
        }

        try {
            startKoin {
                androidLogger(Level.ERROR)
                androidContext(this@MainActivity)
                modules(appModule)
            }
        } catch (e: Exception) {
            Log.d("xavi", "onCreate: $e")
        }
        super.onCreate(savedInstanceState)

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

        setContent {
            var isDark by remember { mutableStateOf(false) }
            val pref = koinInject<PreferencesRepository>()

            LaunchedEffect(isDark) {
                pref.readSettingAsFlow("theme").collect {
                    isDark = it.toBoolean()
                }
            }

            MoshafTheme(isDark) {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenNavController()
                }
            }
        }
    }
}
