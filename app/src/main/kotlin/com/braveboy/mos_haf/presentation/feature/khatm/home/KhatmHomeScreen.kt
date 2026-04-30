package com.braveboy.mos_haf.presentation.feature.khatm.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.presentation.feature.khatm.newkhatm.KhatmType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmHomeScreen(navController: NavController) {
    val viewModel = koinViewModel<KhatmHomeViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_khatm),
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
        var showBottomSheet by remember { mutableStateOf(false) }

        if (showBottomSheet) {
            NewKhatmModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                viewModel.handleIntent(it)
                showBottomSheet = false
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.value.khatms.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    itemsIndexed(
                        state.value.khatms,
                    ) { index, item ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(
                                    width = 1.dp,
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp , bottom = 8.dp),
                                text = item.name,
                                textAlign = TextAlign.Center
                            )
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                progress = { item.completedPages.toFloat() / 664 },
                                color = ProgressIndicatorDefaults.linearColor,
                                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            )
                        }
                    }
                }
            } else {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "ختمی برای نمایش وجود ندارد.",
                )
            }

            FloatingActionButton(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { showBottomSheet = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("ختم جدید")
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewKhatmModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmButton: (KhatmHomeIntent.NewKhatm) -> Unit
) {
    val bottomSheet = rememberStandardBottomSheetState(skipHiddenState = false)
    var khatmName by remember { mutableStateOf("") }
    var selectedKhatmType by remember { mutableStateOf(KhatmType.JOZ) }

    ModalBottomSheet(
        sheetState = bottomSheet,
        onDismissRequest = onDismissRequest
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TextField(
                value = khatmName,
                onValueChange = { khatmName = it },
                label = { Text("عنوان ختم") },
                readOnly = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.small)
                    .border(
                        color = MaterialTheme.colorScheme.primary,
                        width = 1.dp,
                        shape = MaterialTheme.shapes.small
                    )
                    .safeClickable { },
                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "نحوه ختم قرآن :",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.safeClickable { selectedKhatmType = KhatmType.JOZ },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = selectedKhatmType == KhatmType.JOZ,
                    onClick = {
                        selectedKhatmType = KhatmType.JOZ
                    }
                )

                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "جز خوانی",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier.safeClickable { selectedKhatmType = KhatmType.HEZB },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = selectedKhatmType == KhatmType.HEZB,
                    onClick = {
                        selectedKhatmType = KhatmType.HEZB
                    }
                )

                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "حزب خوانی",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier.safeClickable { selectedKhatmType = KhatmType.PAGE },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = selectedKhatmType == KhatmType.PAGE,
                    onClick = {
                        selectedKhatmType = KhatmType.PAGE
                    }
                )

                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "صفحه خوانی",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier.safeClickable { selectedKhatmType = KhatmType.RANGE },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = selectedKhatmType == KhatmType.RANGE,
                    onClick = {
                        selectedKhatmType = KhatmType.RANGE
                    }
                )

                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "بازه خوانی",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    onConfirmButton(
                        KhatmHomeIntent.NewKhatm(
                            name = khatmName,
                            type = selectedKhatmType.name,
                            pages = 0
                        )
                    )
                },
                enabled = khatmName.isNotEmpty()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ایجاد ختم",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}