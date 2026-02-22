package com.braveboy.mos_haf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.braveboy.mos_haf.di.appModule
import com.braveboy.mos_haf.presentation.navigation.ScreenNavController
import com.braveboy.mos_haf.ui.theme.QuranAppTheme
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

        enableEdgeToEdge()
        setContent {
            QuranAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenNavController()
                }
            }
        }
    }
}
