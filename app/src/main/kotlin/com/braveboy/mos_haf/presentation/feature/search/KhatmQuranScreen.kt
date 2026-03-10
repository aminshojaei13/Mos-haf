package com.braveboy.mos_haf.presentation.feature.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.ErrorView
import com.braveboy.mos_haf.components.LoadingIndicator
import com.braveboy.mos_haf.presentation.feature.detail.VerseItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmQuranScreen(navController: NavController) {
    val viewModel = koinViewModel<KhatmQuranViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
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
            }
        )
    }
}

@Composable
fun QuranContent(
    state: KhatmQuranState,
    onIntent: (KhatmQuranIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchBox(state) { find ->
            onIntent(find)
        }

        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorView(
                message = state.error,
                onRetry = { onIntent(KhatmQuranIntent.RefreshData) }
            )

            state.verses.isNotEmpty() -> {
                Log.d("xavi", "verses loaded ***** ${state.verses}")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    itemsIndexed(state.verses) { index, verse ->
                        if (verse.aya == 1 || index == 0) {
                            Text(
                                text = stringResource(R.string.label_bismillah),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (verse.text.isNotBlank()) {
                            VerseItem(
                                arabicText = verse.text,
                                translationText = state.translations[index],
                                ayaNumber = verse.aya.toString(),
                                icon = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBox(
    state: KhatmQuranState,
    onClick: (KhatmQuranIntent) -> Unit
) {
    var startSuraIndex by remember { mutableStateOf<Int?>(null) }
    var endSuraIndex by remember { mutableStateOf<Int?>(null) }
    var startAyaIndex by remember { mutableStateOf<Int?>(0) }
    var endAyaIndex by remember { mutableStateOf<Int?>(0) }
    var jozIndex by remember { mutableStateOf<Int?>(null) }
    var hezbIndex by remember { mutableStateOf<Int?>(null) }
    var showSearchBox by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (showSearchBox) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuraDropdown(
                    label = stringResource(R.string.label_from_surah),
                    suraNames = state.suraNames,
                    selectedSuraIndex = startSuraIndex,
                    onSuraSelected = {
                        Log.d("xavi", it)
                        startSuraIndex = state.suraNames.indexOf(it)
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
                    onSuraSelected = {
                        Log.d("xavi", it)
                        endSuraIndex = state.suraNames.indexOf(it)
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

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JozOrHezbDropdown(
                    label = "جز",
                    selectedIndex = jozIndex,
                    onSelected = { jozIndex = it },
                    modifier = Modifier.weight(1f)
                )

                JozOrHezbDropdown(
                    label = "حزب",
                    selectedIndex = hezbIndex,
                    onSelected = { hezbIndex = it },
                    modifier = Modifier.weight(1f)
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
                    val joz = jozIndex?.plus(1)
                    val hezb = hezbIndex?.plus(1)

                    if (sSura != null && eSura != null && sAya != null && eAya != null) {
                        onClick(
                            KhatmQuranIntent.LoadVersesByDetailedRange(
                                sSura, sAya, eSura, eAya
                            )
                        )
                    } else if (joz != null && hezb != null) {
                        onClick(
                            KhatmQuranIntent.LoadVersesByJozAndHezb(
                                joz, hezb
                            )
                        )
                    }
                    showSearchBox = false
                } else {
                    showSearchBox = true
                }
            },
            enabled = startSuraIndex != null && endSuraIndex != null || jozIndex != null
        ) {
            Text(
                text = stringResource(
                    id = if (showSearchBox) R.string.label_search else R.string.label_search_again
                ),
                style = MaterialTheme.typography.bodyLarge
            )
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
        OutlinedTextField(
            value = selectedSuraName,
            onValueChange = { selectedSuraName = it },
            label = { Text(label) },
            readOnly = false,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            suraNames.map { it.toPersian() }.filter { it.contains(selectedSuraName) }.forEachIndexed { index, name ->
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
        OutlinedTextField(
            value = selectedAyaLabel,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
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
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
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

fun String.toPersian(): String {
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
        'ي' to 'ی',
        'ؤ' to 'ؤ',
        'ئ' to 'ئ',
        'ى' to 'ی',
        'ك' to 'ک'
    )

    return arabicToPersianMap.entries.fold(this) { acc, (ar, fa) ->
        acc.replace(ar,fa)
    }
}

