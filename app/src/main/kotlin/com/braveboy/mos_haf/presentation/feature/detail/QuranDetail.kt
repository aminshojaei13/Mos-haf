package com.braveboy.mos_haf.presentation.feature.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.presentation.common_compose.AyatComponent
import com.braveboy.mos_haf.presentation.feature.player.data.PlayType
import com.braveboy.mos_haf.presentation.feature.player.presentation.PlayerViewController
import com.braveboy.mos_haf.presentation.feature.player.presentation.QuranPlayer
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailScreen(
    navController: NavController,
    viewModel: QuranDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val playerController = koinViewModel<PlayerViewController>()
    val playerState by playerController.state.collectAsState()
    
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
    
    // اسکرول خودکار به آیه در حال پخش
    LaunchedEffect(playerState.currentPlaylistIndex) {
        if (playerState.isPlaying) {
            lazyState.animateScrollToItem(playerState.currentPlaylistIndex + 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.suraName,
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
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

                AyatComponent(
                    verses = state.verses,
                    lazyState = lazyState,
                    translations = state.translations,
                    fontSize = fontSize,
                    suras = state.suraNames,
                    overScrollEnable = true,
                    playingIndex = if (playerState.isPlaying) playerState.currentPlaylistIndex else -1,
                    bookmarked = {
                        viewModel.saveBookmark(
                            LastReadModel(
                                source = "detail",
                                start = it,
                                end = state.verses.last()
                            )
                        )
                    },
                    changeSura = {
                        viewModel.loadVersesAndTranslations(
                            it
                        )
                        viewModel.saveBookmark(
                            LastReadModel(
                                source = null,
                                start = null,
                                end = null
                            )
                        )
                    }
                )

                val ayats = state.verses.map {
                    Pair(it.sura, it.aya)
                }

                QuranPlayer(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    type = PlayType.PLAYLIST(ayats)
                )
            }
        }
    }
}

@Composable
fun VerseItem(
    modifier: Modifier = Modifier,
    arabicText: String,
    translationText: String,
    ayaNumber: String,
    fontSize: TextUnit,
    icon: Int? = null,
    isHighlighted: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isHighlighted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .animateContentSize()
    ) {
        Box {
            Row(verticalAlignment = Alignment.Top) {
                icon?.let {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 32.dp),
                        painter = painterResource(id = it), contentDescription = null
                    )
                }

                Text(
                    text = "$arabicText (${ayaNumber.toPersianNumber()})",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    fontSize = fontSize
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = translationText,
            modifier = Modifier
                .fillMaxWidth()
                .safeClickable { isExpanded = !isExpanded },
            style = MaterialTheme.typography.bodyLarge,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuranDetailScreenPreview() {
    MoshafTheme(false) {
        QuranDetailScreen(
            navController = rememberNavController()
        )
    }
}

fun String.toPersianNumber(): String {
    val tr = mapOf(
        "0" to "۰",
        "1" to "۱",
        "2" to "۲",
        "3" to "۳",
        "4" to "۴",
        "5" to "۵",
        "6" to "۶",
        "7" to "۷",
        "8" to "۸",
        "9" to "۹"
    )
    return tr.entries.fold(this) { acc, (en, fa) ->
        acc.replace(en, fa)
    }
}

fun String.toPersianWord(): String {
    val arabicToPersianMap = mapOf(
        'ا' to 'ا',
        'إ' to 'ا',
        'أ' to 'ا',
        'ب' to 'ب',
        'پ' to 'پ',
        'ت' to 'ت',
        'ث' to 'ث',
        'ج' to 'ج',
        'چ' to 'چ',
        'ح' to 'ح',
        'خ' to 'خ',
        'د' to 'د',
        'ذ' to 'ذ',
        'ر' to 'ر',
        'ز' to 'ز',
        'ژ' to 'ژ',
        'س' to 'س',
        'ش' to 'ش',
        'ص' to 'ص',
        'ض' to 'ض',
        'ط' to 'ط',
        'ظ' to 'ظ',
        'ع' to 'ع',
        'غ' to 'غ',
        'ف' to 'ف',
        'ق' to 'ق',
        'ک' to 'ک',
        'گ' to 'گ',
        'ل' to 'ل',
        'م' to 'م',
        'ن' to 'ن',
        'و' to 'و',
        'ه' to 'ه',
        'ة' to 'ه',
        'ي' to 'ی',
        'ؤ' to 'ؤ',
        'ئ' to 'ئ',
        'ى' to 'ی',
        'ك' to 'ک'
    )

    return arabicToPersianMap.entries.fold(this) { acc, (ar, fa) ->
        acc.replace(ar, fa)
    }
}

fun String.toArabicWord(): String {
    val arabicToPersianMap = mapOf(
        'ا' to 'ا',
        'ا' to 'إ',
        'ا' to 'أ',
        'ب' to 'ب',
        'پ' to 'پ',
        'ت' to 'ت',
        'ث' to 'ث',
        'ج' to 'ج',
        'چ' to 'چ',
        'ح' to 'ح',
        'خ' to 'خ',
        'د' to 'د',
        'ذ' to 'ذ',
        'ر' to 'ر',
        'ز' to 'ز',
        'ژ' to 'ژ',
        'س' to 'س',
        'ش' to 'ش',
        'ص' to 'ص',
        'ض' to 'ض',
        'ط' to 'ط',
        'ظ' to 'ظ',
        'ع' to 'ع',
        'غ' to 'غ',
        'ف' to 'ف',
        'ق' to 'ق',
        'ک' to 'ک',
        'گ' to 'گ',
        'ل' to 'ل',
        'م' to 'م',
        'ن' to 'ن',
        'و' to 'و',
        'ه' to 'ه',
        'ی' to 'ي',
        'ؤ' to 'ؤ',
        'ئ' to 'ئ',
        'ى' to 'ی',
        'ک' to 'ك'
    )

    return arabicToPersianMap.entries.fold(this) { acc, (ar, fa) ->
        acc.replace(ar, fa)
    }
}
