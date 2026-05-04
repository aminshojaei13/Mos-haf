package com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.presentation.feature.detail.toPersianNumber
import com.braveboy.mos_haf.presentation.feature.khatm.model.KhatmVersesModel
import com.braveboy.mos_haf.presentation.navigation.Screen
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmDetailScreen(navController: NavController) {
    val viewModel = koinViewModel<KhatmDetailViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_khatm_detail),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->

        val lazyState = rememberLazyListState()

        LaunchedEffect(state.value.suraNames) {
            val index = if (state.value.khatmDetail?.type == KhatmType.HEZB.name) {
                state.value.suraNames.lastOrNull { quran ->
                    quran.page == state.value.khatmDetail?.completedPages
                }?.hezb ?: 0
            } else if (state.value.khatmDetail?.type == KhatmType.JOZ.name) {
                state.value.suraNames.lastOrNull { quran ->
                    quran.page == state.value.khatmDetail?.completedPages
                }?.juz ?: 0
            } else {
                state.value.suraNames.lastOrNull { quran ->
                    quran.page == state.value.khatmDetail?.completedPages
                }?.page ?: 0
            }

            lazyState.animateScrollToItem(
                index
            )
        }

        state.value.khatmDetail?.let { khatmDetail ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                state = lazyState
            ) {
                when (khatmDetail.type) {
                    KhatmType.JOZ.name -> {
                        items(30) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(16.dp)
                                    .safeClickable {
                                        val khatm = Json.encodeToString(
                                            KhatmVersesModel(
                                                id = state.value.khatmDetail?.id,
                                                type = KhatmType.JOZ.name,
                                                joz = it + 1
                                            )
                                        )
                                        navController.navigate(
                                            Screen.KhatmVerses(
                                                khatm = khatm,
                                                fromLast = false
                                            )
                                        )
                                    },
                                text = "خوانش جز : " + (it + 1).toString().toPersianNumber(),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    KhatmType.HEZB.name -> {
                        items(120) {
                            val joz = (it / 4) + 1
                            val hezb = (it % 4) + 1

                            Log.d(
                                "xavi",
                                "KhatmDetailScreen22: ${state.value.suraNames.firstOrNull { quran -> quran.hezb == it }?.page}"
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .background(
                                        color = if (state.value.khatmDetail!!.completedPages > (state.value.suraNames.firstOrNull { quran -> quran.hezb == it }?.page
                                                ?: 0)
                                        )
                                            Color.Gray.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                    .padding(16.dp)
                                    .safeClickable {
                                        val khatm = Json.encodeToString(
                                            KhatmVersesModel(
                                                id = state.value.khatmDetail?.id,
                                                type = KhatmType.HEZB.name,
                                                hezb = it + 1
                                            )
                                        )
                                        navController.navigate(
                                            Screen.KhatmVerses(
                                                khatm = khatm,
                                                fromLast = false
                                            )
                                        )
                                    },
                                text = "خوانش جز : " + joz.toString().toPersianNumber() +
                                        " - حزب : " + hezb.toString().toPersianNumber(),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    KhatmType.PAGE.name -> {
                        items(604) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .background(
                                        color = if (state.value.khatmDetail!!.completedPages > it)
                                            Color.Gray.copy(alpha = 0.3f)
                                        else Color.Transparent
                                    )
                                    .padding(16.dp)
                                    .safeClickable {
                                        val khatm = Json.encodeToString(
                                            KhatmVersesModel(
                                                id = state.value.khatmDetail?.id,
                                                type = KhatmType.PAGE.name,
                                                page = it + 1
                                            )
                                        )
                                        navController.navigate(
                                            Screen.KhatmVerses(
                                                khatm = khatm,
                                                fromLast = false
                                            )
                                        )
                                    },
                                text = "خوانش صفحه : " + (it + 1).toString().toPersianNumber(),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "خطا در نمایش اطلاعات.",
                )
            }
        }
    }

}

enum class KhatmType {
    JOZ,
    HEZB,
    PAGE,
}