package com.braveboy.mos_haf.presentation.feature.search

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.ErrorView
import com.braveboy.mos_haf.components.LoadingIndicator
import com.braveboy.mos_haf.presentation.feature.detail.VerseItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmQuranScreen() {
    val viewModel = koinViewModel<KhatmQuranViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.label_top_app_bar)) }
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
    var startSuraIndex by remember { mutableStateOf<Int?>(null) }
    var endSuraIndex by remember { mutableStateOf<Int?>(null) }
    var startAyaIndex by remember { mutableStateOf<Int?>(0) }
    var endAyaIndex by remember { mutableStateOf<Int?>(0) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Search Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Start Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuraDropdown(
                        label = "از سوره",
                        suraNames = state.suraNames,
                        selectedSuraIndex = startSuraIndex,
                        onSuraSelected = {
                            startSuraIndex = it
                            startAyaIndex = 0
                        },
                        modifier = Modifier.weight(1f)
                    )

                    AyaDropdown(
                        label = "آیه",
                        ayaCount = startSuraIndex?.let { state.ayaCounts.getOrNull(it) } ?: 0,
                        selectedAyaIndex = startAyaIndex,
                        onAyaSelected = { startAyaIndex = it },
                        modifier = Modifier.weight(0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuraDropdown(
                        label = "تا سوره",
                        suraNames = state.suraNames,
                        selectedSuraIndex = endSuraIndex,
                        onSuraSelected = {
                            endSuraIndex = it
                            endAyaIndex = 0
                        },
                        modifier = Modifier.weight(1f)
                    )

                    AyaDropdown(
                        label = "آیه",
                        ayaCount = endSuraIndex?.let { state.ayaCounts.getOrNull(it) } ?: 0,
                        selectedAyaIndex = endAyaIndex,
                        onAyaSelected = { endAyaIndex = it },
                        modifier = Modifier.weight(0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val sSura = startSuraIndex?.plus(1)
                        val eSura = endSuraIndex?.plus(1)
                        val sAya = startAyaIndex?.plus(1)
                        val eAya = endAyaIndex?.plus(1)

                        if (sSura != null && eSura != null && sAya != null && eAya != null) {
                            onIntent(
                                KhatmQuranIntent.LoadVersesByDetailedRange(
                                    sSura, sAya, eSura, eAya
                                )
                            )
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = startSuraIndex != null && endSuraIndex != null
                ) {
                    Text("جستجو")
                }
            }
        }

        // Results
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorView(
                message = state.error,
                onRetry = { onIntent(KhatmQuranIntent.RefreshData) }
            )

            state.verses.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.label_bismillah),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    itemsIndexed(state.verses) { _, verse ->
                        if (verse.text.isNotBlank()) {
                            VerseItem(
                                arabicText = verse.text,
                                translationText = "state.translations.getOrNull(index) ?: "
                            )
                        }
                    }
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
    onSuraSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedSuraName = selectedSuraIndex?.let { suraNames.getOrNull(it) } ?: ""

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedSuraName,
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
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            suraNames.forEachIndexed { index, name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSuraSelected(index)
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
