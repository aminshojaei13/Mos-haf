package com.braveboy.mos_haf.presentation.feature.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.ErrorView
import com.braveboy.mos_haf.components.LoadingIndicator
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.presentation.common_compose.AyatComponent
import com.braveboy.mos_haf.presentation.feature.detail.toPersianWord
import com.braveboy.mos_haf.presentation.feature.search.SearchIntent.SaveBookmark
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val viewModel = koinViewModel<SearchViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val sliderState = rememberSliderState(value = 0.5f, steps = 5)
    var expandedFontSize by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(28.sp) }

    LaunchedEffect(state.value.fontSize) {
        Log.d("xavi", "QuranDetailScreen: ${state.value.fontSize}")
        sliderState.value = state.value.fontSize ?: sliderState.value
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_search),
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
                                    text = if (state.value.isDarkMode) "حالت روشن " else "حالت تیره ",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .safeClickable {
                                            viewModel.handleIntent(SearchIntent.SaveTheme(!state.value.isDarkMode))
                                        },
                                    imageVector = if (state.value.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
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
        QuranContent(
            modifier = Modifier.padding(paddingValues),
            state = state.value,
            onIntent = {
                viewModel.handleIntent(it)
            },
            fontSize = fontSize
        )
    }
}

@SuppressLint("FrequentlyChangingValue")
@Composable
fun QuranContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val lazyState = rememberLazyListState()

        LaunchedEffect(lazyState.firstVisibleItemIndex) {
            if (state.verses.isNotEmpty() && lazyState.layoutInfo.visibleItemsInfo.last().index == state.verses.lastIndex) {
                onIntent(
                    SaveBookmark(
                        LastReadModel(
                            source = null,
                            start = null,
                            end = null
                        )
                    )
                )
            }
        }

        SearchBox(state) { find ->
            onIntent(find)
        }

        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorView(
                message = state.error,
                onRetry = { onIntent(SearchIntent.RefreshData) }
            )

            state.verses.isNotEmpty() -> {
                AyatComponent(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp),
                    lazyState = lazyState,
                    verses = state.verses,
                    translations = state.translations,
                    fontSize = fontSize ?: 28.sp,
                    overScrollEnable = false,
                    bookmarked = {
                        onIntent(
                            SaveBookmark(
                                LastReadModel(
                                    source = "search",
                                    start = it,
                                    end = state.verses.last()
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBox(
    state: SearchState,
    onClick: (SearchIntent) -> Unit
) {
    var suraIndex by remember { mutableStateOf<Int?>(null) }
    var startSuraIndex by remember { mutableStateOf<Int?>(null) }
    var endSuraIndex by remember { mutableStateOf<Int?>(null) }
    var startAyaIndex by remember { mutableStateOf<Int?>(null) }
    var endAyaIndex by remember { mutableStateOf<Int?>(null) }
    var jozIndex by remember { mutableStateOf<Int?>(null) }
    var hezbIndex by remember { mutableStateOf<Int?>(null) }
    var showSearchBox by remember { mutableStateOf(true) }

    LaunchedEffect(state.lastRead?.start) {
        if (state.lastRead?.start != null) {
            showSearchBox = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (showSearchBox) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SuraDropdown(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.label_sura),
                    suraNames = state.suraNames,
                    selectedSuraIndex = suraIndex,
                    onSuraSelected = { sura ->
                        suraIndex = state.suraNames.map { it.toPersianWord() }.indexOf(sura)
                    },
                )

                Spacer(Modifier.width(4.dp))

                OutlinedIconButton(
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.small,
                    enabled = suraIndex != null,
                    onClick = {
                        val sura = suraIndex
                        if (sura != null) {
                            onClick(
                                SearchIntent.LoadVersesBySura(sura)
                            )
                            suraIndex = null
                        }
                        showSearchBox = false
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JozOrHezbDropdown(
                    modifier = Modifier.weight(1f),
                    label = "جز",
                    selectedIndex = jozIndex,
                    onSelected = { jozIndex = it }
                )

                JozOrHezbDropdown(
                    label = "حزب",
                    selectedIndex = hezbIndex,
                    onSelected = { hezbIndex = it },
                    modifier = Modifier.weight(1f)
                )

                OutlinedIconButton(
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.small,
                    enabled = hezbIndex != null && jozIndex != null,
                    onClick = {
                        val joz = jozIndex?.plus(1)
                        val hezb = hezbIndex?.plus(1)

                        if (joz != null && hezb != null) {
                            onClick(
                                SearchIntent.LoadVersesByJozAndHezb(
                                    joz, hezb
                                )
                            )
                            jozIndex = null
                            hezbIndex = null
                        }
                        showSearchBox = false
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuraDropdown(
                    label = stringResource(R.string.label_from_surah),
                    suraNames = state.suraNames,
                    selectedSuraIndex = startSuraIndex,
                    onSuraSelected = { sura ->
                        startSuraIndex = state.suraNames.map { it.toPersianWord() }.indexOf(sura)
                        startAyaIndex = 0
                    },
                    modifier = Modifier.weight(1f)
                )

                AyaDropdown(
                    label = stringResource(R.string.label_aya),
                    ayaCount = startSuraIndex?.let { state.ayaCounts.getOrNull(it) } ?: 0,
                    selectedAyaIndex = startAyaIndex,
                    onAyaSelected = { startAyaIndex = it },
                    modifier = Modifier.weight(0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuraDropdown(
                    label = stringResource(R.string.label_to_surah),
                    suraNames = state.suraNames,
                    selectedSuraIndex = endSuraIndex,
                    onSuraSelected = { sura ->
                        Log.d("xavi", sura)
                        endSuraIndex = state.suraNames.map { it.toPersianWord() }.indexOf(sura)
                        endAyaIndex = 0
                    },
                    modifier = Modifier.weight(1f)
                )

                AyaDropdown(
                    label = "آیه",
                    ayaCount = endSuraIndex?.let { state.ayaCounts.getOrNull(it) } ?: 0,
                    selectedAyaIndex = endAyaIndex,
                    onAyaSelected = { endAyaIndex = it },
                    modifier = Modifier.weight(0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.CenterHorizontally),
            shape = MaterialTheme.shapes.small,
            onClick = {
                if (showSearchBox) {
                    val sSura = startSuraIndex?.plus(1)
                    val eSura = endSuraIndex?.plus(1)
                    val sAya = startAyaIndex?.plus(1)
                    val eAya = endAyaIndex?.plus(1)

                    if (sSura != null && eSura != null && sAya != null && eAya != null) {
                        onClick(
                            SearchIntent.LoadVersesByDetailedRange(
                                sSura, sAya, eSura, eAya
                            )
                        )
                        startSuraIndex = null
                        endSuraIndex = null
                        startAyaIndex = null
                        endAyaIndex = null
                    }
                    showSearchBox = false
                } else {
                    showSearchBox = true
                }
            },
            enabled = startSuraIndex != null && endSuraIndex != null || !showSearchBox
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(
                        id = if (showSearchBox) R.string.label_search else R.string.label_search_again
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                if (showSearchBox) {
                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuraDropdown(
    label: String,
    suraNames: List<String>,
    selectedSuraIndex: Int?,
    onSuraSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSuraName by remember(selectedSuraIndex) {
        mutableStateOf(
            selectedSuraIndex?.let { suraNames.getOrNull(it) } ?: ""
        )
    }

    Box(modifier = modifier) {
        TextField(
            value = selectedSuraName.trim(),
            onValueChange = { selectedSuraName = it },
            label = { Text(label) },
            readOnly = false,
            maxLines = 1,
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(selectedSuraName.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .safeClickable { selectedSuraName = "" }
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.safeClickable { expanded = !expanded }
                    )
                }
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .border(
                    color = MaterialTheme.colorScheme.primary,
                    width = 1.dp,
                    shape = MaterialTheme.shapes.small
                )
                .safeClickable { expanded = !expanded },
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            suraNames.map { it.toPersianWord() }
                .filter { it.contains(selectedSuraName.toPersianWord()) }
                .forEachIndexed { _, name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onSuraSelected(name)
                            expanded = false
                        }
                    )
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyaDropdown(
    label: String,
    ayaCount: Int,
    selectedAyaIndex: Int?,
    onAyaSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAyaLabel = selectedAyaIndex?.let { (it + 1).toString() } ?: ""

    Box(modifier = modifier) {
        TextField(
            value = selectedAyaLabel,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            maxLines = 1,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.safeClickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .border(
                    color = MaterialTheme.colorScheme.primary,
                    width = 1.dp,
                    shape = MaterialTheme.shapes.small
                )
                .safeClickable { expanded = !expanded },
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            repeat(ayaCount) { index ->
                DropdownMenuItem(
                    text = { Text((index + 1).toString()) },
                    onClick = {
                        onAyaSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JozOrHezbDropdown(
    label: String,
    selectedIndex: Int?,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = selectedIndex?.let { (it + 1).toString() } ?: ""

    Box(modifier = modifier) {
        TextField(
            value = selectedLabel,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            maxLines = 1,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.safeClickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .border(
                    color = MaterialTheme.colorScheme.primary,
                    width = 1.dp,
                    shape = MaterialTheme.shapes.small
                )
                .fillMaxWidth()
                .safeClickable { expanded = !expanded },
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val count = if (label == "جز") 30 else 4
            repeat(count) { index ->
                DropdownMenuItem(
                    text = { Text((index + 1).toString()) },
                    onClick = {
                        onSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

