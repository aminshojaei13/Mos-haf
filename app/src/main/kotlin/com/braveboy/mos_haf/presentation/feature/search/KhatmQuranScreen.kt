package com.braveboy.mos_haf.presentation.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.ErrorView
import com.braveboy.mos_haf.components.LoadingIndicator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmQuranScreen() {
    val viewModel = koinViewModel<QuranViewModel>()
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
    var suraInput by remember { mutableStateOf("") }
    var pageInput by remember { mutableStateOf("") }

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
                // Sura search
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = suraInput,
                        onValueChange = { suraInput = it },
                        label = { Text("رقم السورة") },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            suraInput.toIntOrNull()?.let {
                                onIntent(KhatmQuranIntent.LoadVersesBySura(it))
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.outline_14mp_24), contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Page search
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pageInput,
                        onValueChange = { pageInput = it },
                        label = { Text("رقم الصفحة") },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            pageInput.toIntOrNull()?.let {
                                onIntent(KhatmQuranIntent.LoadVersesByPage(it))
                            }
                        }
                    ) {
                        Icon(painterResource(R.drawable.outline_14mp_24), contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Refresh button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onIntent(KhatmQuranIntent.RefreshData) }) {
                        Text("تحديث")
                    }
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

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.verses) { verse ->
                        QuranVerseCard(verse = verse)
                    }
                }
            }
        }
    }
}