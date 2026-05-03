package com.braveboy.mos_haf.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.braveboy.mos_haf.presentation.feature.detail.QuranDetailScreen
import com.braveboy.mos_haf.presentation.feature.home.HomeScreen
import com.braveboy.mos_haf.presentation.feature.khatm.home.KhatmHomeScreen
import com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail.KhatmDetailScreen
import com.braveboy.mos_haf.presentation.feature.khatm.khatmverses.KhatmVersesScreen
import com.braveboy.mos_haf.presentation.feature.search.SearchScreen
import com.braveboy.mos_haf.presentation.feature.suralist.SuraListScreen
import com.braveboy.mos_haf.presentation.navigation.Screen.QuranDetail

@Composable
fun ScreenNavController() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }

            composable(Screen.SuraList.route) {
                SuraListScreen(navController)
            }

            composable<QuranDetail> { backStackEntry ->
                val suraName: QuranDetail = backStackEntry.toRoute()
                QuranDetailScreen(
                    navController = navController
                )
            }

            composable<Screen.Search> { backStackEntry ->
                val from: Screen.Search = backStackEntry.toRoute()
                SearchScreen(navController)
            }

            composable(Screen.KhatmHome.route) {
                KhatmHomeScreen(navController)
            }

            composable<Screen.KhatmDetail> { backStackEntry ->
                val khatmId: Screen.KhatmDetail = backStackEntry.toRoute()
                KhatmDetailScreen(
                    navController = navController
                )
            }

            composable<Screen.KhatmVerses> { backStackEntry ->
                val khatm: Screen.KhatmVerses = backStackEntry.toRoute()
                KhatmVersesScreen(
                    navController = navController
                )
            }
        }
    }
}