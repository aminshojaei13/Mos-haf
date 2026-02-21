package com.braveboy.mos_haf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.braveboy.mos_haf.di.appModule
import com.braveboy.mos_haf.presentation.navigation.ScreenNavController
import com.example.quranapp.presentation.theme.QuranAppTheme
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
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                QuranAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ScreenNavController()
                    }
                }
            }
        }
    }
}
