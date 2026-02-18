package com.braveboy.mos_haf

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold{ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "quran",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("quran") {
                QuranScreen()
            }
        }
    }
}