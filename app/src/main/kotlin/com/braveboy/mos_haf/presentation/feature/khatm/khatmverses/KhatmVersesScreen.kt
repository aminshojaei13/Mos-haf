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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.presentation.common_compose.AyatComponent
import com.braveboy.mos_haf.presentation.feature.detail.toPersianNumber
import com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail.KhatmType
import com.braveboy.mos_haf.presentation.feature.khatm.model.KhatmVersesModel
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import com.braveboy.mos_haf.presentation.feature.player.model.PlayerState
import com.braveboy.mos_haf.presentation.feature.player.presentation.PlayerViewController
import com.braveboy.mos_haf.presentation.feature.player.presentation.QuranPlayer
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun KhatmVersesScreen(
    navController: NavController,
    viewModel: KhatmVersesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val playerController = koinViewModel<PlayerViewController>()
    val playerState by playerController.state.collectAsState()

    KhatmVersesContent(
        state = state,
        playerState = playerState,
        onNavigateUp = { navController.navigateUp() },
        onSaveFontSize = { viewModel.saveFontSize(it) },
        onSaveTheme = { viewModel.saveTheme(it) },
        onSaveBookmark = { viewModel.saveBookmark(it) },
        onLoadAnotherVerse = { viewModel.handleIntent(KhatmVersesIntent.LoadAnotherVerse(it)) },
        onSaveCompleteReadPage = { viewModel.handleIntent(KhatmVersesIntent.SaveCompleteReadPage) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmVersesContent(
    state: KhatmVersesState,
    playerState: PlayerState,
    onNavigateUp: () -> Unit,
    onSaveFontSize: (Float) -> Unit,
    onSaveTheme: (Boolean) -> Unit,
    onSaveBookmark: (LastReadModel) -> Unit,
    onLoadAnotherVerse: (Int) -> Unit,
    onSaveCompleteReadPage: () -> Unit
) {
    var expandedFontSize by remember { mutableStateOf(false) }
    val sliderState = rememberSliderState(value = 0.5f, steps = 5)
    var fontSize by remember { mutableStateOf(28.sp) }
    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(state.fontSize) {
        sliderState.value = state.fontSize ?: sliderState.value
    }

    LaunchedEffect(sliderState.value) {
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

    LaunchedEffect(state.lastRead?.start) {
        state.lastRead?.start?.let {
            loading = true
            lazyState.animateScrollToItem(it.aya)
            loading = false
        }
    }

    // اسکرول خودکار به آیه در حال پخش
    LaunchedEffect(playerState.currentPlaylistIndex) {
        if (playerState.isPlaying && playerState.currentPlaylistIndex in state.verses.indices) {
            lazyState.animateScrollToItem(playerState.currentPlaylistIndex)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val khatmName = state.khatmDetail?.name ?: ""
                    val type = state.khatmDetail?.type
                    val khatmInfo = state.khatm
                    val titleSuffix = when (type) {
                        KhatmType.JOZ.name -> "جز " + (khatmInfo?.joz?.toString() ?: "").toPersianNumber()
                        KhatmType.HEZB.name -> "حزب " + (khatmInfo?.hezb?.toString() ?: "").toPersianNumber()
                        KhatmType.PAGE.name -> "صفحه " + (khatmInfo?.page?.toString() ?: "").toPersianNumber()
                        else -> ""
                    }
                    Text(
                        text = "$khatmName $titleSuffix".trim(),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
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
                                onSaveFontSize(sliderState.value)
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
                                            onSaveTheme(!state.isDarkMode)
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
                CircularProgressIndicator(modifier = Modifier.size(64.dp).align(Alignment.Center))
            } else {
                if (loading) {
                    Dialog(onDismissRequest = { },) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                }

                AyatComponent(
                    verses = state.verses,
                    lazyState = lazyState,
                    translations = state.translations,
                    fontSize = fontSize,
                    suras = state.suraNames,
                    overScrollEnable = true,
                    playingIndex = if (playerState.isPlaying) playerState.currentPlaylistIndex else -1,
                    bookmarked = {
                        onSaveBookmark(
                            LastReadModel(
                                id = state.khatmDetail?.id,
                                source = state.khatm?.type,
                                start = it,
                                end = state.verses.last()
                            )
                        )
                    },
                    backwardItem = {
                        state.khatm?.let { khatmInfo ->
                            val joz = khatmInfo.joz ?: 0
                            val hezb = khatmInfo.hezb ?: 0
                            val page = khatmInfo.page ?: 0
                            if (joz > 1 || hezb > 1 || page > 1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .safeClickable {
                                            onLoadAnotherVerse(
                                                when (khatmInfo.type) {
                                                    KhatmType.JOZ.name -> joz - 1
                                                    KhatmType.HEZB.name -> hezb - 1
                                                    KhatmType.PAGE.name -> page - 1
                                                    else -> 0
                                                }
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
                                        text = when (khatmInfo.type) {
                                            KhatmType.JOZ.name -> "جز قبلی"
                                            KhatmType.HEZB.name -> "حزب قبلی"
                                            KhatmType.PAGE.name -> "صفحه قبلی"
                                            else -> ""
                                        },
                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }
                        }
                    },
                    forwardItem = {
                        onSaveCompleteReadPage()
                        state.khatm?.let { khatmInfo ->
                            val joz = khatmInfo.joz ?: 0
                            val hezb = khatmInfo.hezb ?: 0
                            val page = khatmInfo.page ?: 0
                            if (joz < 30 || hezb < 120 || page < 604) {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .fillMaxWidth()
                                        .safeClickable {
                                            onLoadAnotherVerse(
                                                when (khatmInfo.type) {
                                                    KhatmType.JOZ.name -> joz + 1
                                                    KhatmType.HEZB.name -> hezb + 1
                                                    KhatmType.PAGE.name -> page + 1
                                                    else -> 0
                                                }
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
                                        text = when (khatmInfo.type) {
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
                    }
                )

                val ayats = state.verses.map { Pair(it.sura, it.aya) }
                
                val startIndex = state.lastRead?.start?.let { lastRead ->
                    state.verses.indexOfFirst { it.sura == lastRead.sura && it.aya == lastRead.aya }
                }.takeIf { it != null && it != -1 } ?: 0

                QuranPlayer(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    type = PlayType.PLAYLIST(ayats, startIndex)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KhatmVersesScreenPreview() {
    val dummyState = KhatmVersesState(
        isLoading = false,
        khatm = KhatmVersesModel(type = KhatmType.JOZ.name, joz = 1),
        khatmDetail = KhatmEntity(
            id = 1,
            name = "ختم قرآن",
            type = KhatmType.JOZ.name,
            completedPages = 0,
            startDate = 1715454000000L,
            lastReadDate = 1715454000000L,
            isActive = true
        ),
        verses = listOf(
            Quran(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ", "الفاتحة", 1, 1, 1),
            Quran(2, 1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الفاتحة", 1, 1, 1),
            Quran(3, 1, 3, "الرَّحْمَنِ الرَّحِيمِ", "الفاتحة", 1, 1, 1),
            Quran(4, 1, 4, "مَالِكِ يَوْمِ الدِّينِ", "الفاتحة", 1, 1, 1)
        ),
        translations = listOf(
            "به نام خداوند بخشنده مهربان",
            "ستایش مخصوص خداوندی است که پروردگار جهانیان است",
            "بخشنده و مهربان است",
            "مالک روز جزاست"
        ),
        suraNames = listOf("الفاتحة"),
        isDarkMode = false,
        fontSize = 0.5f
    )

    MoshafTheme(false) {
        KhatmVersesContent(
            state = dummyState,
            playerState = PlayerState(),
            onNavigateUp = {},
            onSaveFontSize = {},
            onSaveTheme = {},
            onSaveBookmark = {},
            onLoadAnotherVerse = {},
            onSaveCompleteReadPage = {}
        )
    }
}
