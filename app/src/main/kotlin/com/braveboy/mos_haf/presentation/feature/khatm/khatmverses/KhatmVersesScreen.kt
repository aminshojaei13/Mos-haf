package com.braveboy.mos_haf.presentation.feature.khatm.khatmverses

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.presentation.common_compose.AyatComponent
import com.braveboy.mos_haf.presentation.feature.detail.toPersianNumber
import com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail.KhatmType
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import com.braveboy.mos_haf.presentation.feature.player.presentation.QuranPlayer
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmVersesScreen(
    navController: NavController,
    viewModel: KhatmVersesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var expandedFontSize by remember { mutableStateOf(false) }
    val sliderState = rememberSliderState(value = 0.5f, steps = 5)
    var fontSize by remember { mutableStateOf(28.sp) }
    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.fontSize) {
        sliderState.value = state.fontSize ?: sliderState.value
    }

    LaunchedEffect(sliderState.value) {
        sliderState.onValueChange.let {
            when (sliderState.value) {
                0.0f -> fontSize = 18.sp
                0.16666667f -> fontSize = 20.sp
                0.33333334f -> fontSize = 24.sp
                0.5f -> fontSize = 28.sp
                0.6666667f -> fontSize = 32.sp
                0.8333333f -> fontSize = 36.sp
                1.0f -> fontSize = 40.sp
            }
        }
    }

    LaunchedEffect(true) {
        if (state.lastRead?.start != null) {
            loading = true
            lazyState.animateScrollToItem(state.lastRead?.start?.aya ?: 0)
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val name = state.khatmDetail?.name + " " + when (state.khatmDetail?.type) {
                        KhatmType.JOZ.name -> "جز " + state.khatm?.joz.toString().toPersianNumber()
                        KhatmType.HEZB.name -> "حزب " + state.khatm?.hezb.toString()
                            .toPersianNumber()

                        KhatmType.PAGE.name -> "صفحه " + state.khatm?.page.toString()
                            .toPersianNumber()

                        else -> {
                            Log.d("xavi", "KhatmVersesScreen: name not in khatm type")
                        }
                    }
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            expandedFontSize = true
                        }
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")

                        DropdownMenu(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.large,
                            expanded = expandedFontSize,
                            onDismissRequest = {
                                viewModel.saveFontSize(sliderState.value)
                                expandedFontSize = false
                            },
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (state.isDarkMode) "حالت روشن " else "حالت تیره ",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .safeClickable {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.saveTheme(!state.isDarkMode)
                                            }
                                        },
                                    imageVector = if (state.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null
                                )
                            }

                            HorizontalDivider(
                                Modifier.padding(vertical = 8.dp),
                                DividerDefaults.Thickness,
                                DividerDefaults.color
                            )

                            Text(
                                text = stringResource(R.string.label_font_size),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(Modifier.height(8.dp))

                            Slider(
                                state = sliderState,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            } else {
                if (loading) {
                    Dialog(
                        onDismissRequest = { },
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                }

                val detail = Pair(
                    first = when (state.khatm?.type) {
                        PlayType.JOZ.name -> PlayType.JOZ
                        PlayType.HEZB.name -> PlayType.HEZB
                        PlayType.PAGE.name -> PlayType.PAGE
                        else -> PlayType.SURAH
                    },
                    second = when (state.khatm?.type) {
                        PlayType.JOZ.name -> state.khatm!!.joz
                        PlayType.HEZB.name -> state.khatm!!.hezb
                        PlayType.PAGE.name -> state.khatm!!.page
                        else -> 0
                    }
                )

                AyatComponent(
                    verses = state.verses,
                    lazyState = lazyState,
                    translations = state.translations,
                    fontSize = fontSize,
                    suras = state.suraNames,
                    overScrollEnable = true,
                    bookmarked = {
                        viewModel.saveBookmark(
                            LastReadModel(
                                id = state.khatmDetail?.id,
                                source = state.khatm?.type,
                                start = it,
                                end = state.verses.last()
                            )
                        )
                    },
                    backwardItem = {
                        if (
                            state.khatm?.joz != null && state.khatm?.joz!! > 1 ||
                            state.khatm?.hezb != null && state.khatm?.hezb!! > 1 ||
                            state.khatm?.page != null && state.khatm?.page!! > 1
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .safeClickable {
                                        viewModel.handleIntent(
                                            KhatmVersesIntent.LoadAnotherVerse(
                                                when (state.khatm?.type) {
                                                    KhatmType.JOZ.name -> state.khatm?.joz?.minus(1)
                                                        ?: state.khatm?.joz

                                                    KhatmType.HEZB.name -> state.khatm?.hezb?.minus(
                                                        1
                                                    )
                                                        ?: state.khatm?.hezb

                                                    KhatmType.PAGE.name -> state.khatm?.page?.minus(
                                                        1
                                                    )
                                                        ?: state.khatm?.page

                                                    else -> {}
                                                } as Int
                                            )
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowCircleUp,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = when (state.khatm?.type) {
                                        KhatmType.JOZ.name -> "جز قبلی"
                                        KhatmType.HEZB.name -> "حزب قبلی"
                                        KhatmType.PAGE.name -> "صفحه قبلی"
                                        else -> ""
                                    },
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    },
                    forwardItem = {
                        viewModel.handleIntent(KhatmVersesIntent.SaveCompleteReadPage)
                        if (
                            state.khatm?.joz != null && state.khatm?.joz!! < 30 ||
                            state.khatm?.hezb != null && state.khatm?.hezb!! < 120 ||
                            state.khatm?.page != null && state.khatm?.page!! < 604
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth()
                                    .safeClickable {
                                        viewModel.handleIntent(
                                            KhatmVersesIntent.LoadAnotherVerse(
                                                when (state.khatm?.type) {
                                                    KhatmType.JOZ.name -> state.khatm?.joz?.plus(1)
                                                        ?: state.khatm?.joz

                                                    KhatmType.HEZB.name -> state.khatm?.hezb?.plus(1)
                                                        ?: state.khatm?.hezb

                                                    KhatmType.PAGE.name -> state.khatm?.page?.plus(1)
                                                        ?: state.khatm?.page

                                                    else -> {}
                                                } as Int
                                            )
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowCircleDown,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = when (state.khatm?.type) {
                                        KhatmType.JOZ.name -> "جز بعدی"
                                        KhatmType.HEZB.name -> "حزب بعدی"
                                        KhatmType.PAGE.name -> "صفحه بعدی"
                                        else -> ""
                                    },
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                )

                val ayats = state.verses.map {
                    Pair(it.sura, it.aya)
                }

                QuranPlayer(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    type = detail.first,
                    id = detail.second ?: 0
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KhatmVersesScreenPreview() {
    MoshafTheme(false) {
        KhatmVersesScreen(
            navController = rememberNavController()
        )
    }
}
