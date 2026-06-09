package com.braveboy.mos_haf.presentation.feature.khatm.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail.KhatmType
import com.braveboy.mos_haf.presentation.navigation.Screen
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
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        var showBottomSheet by remember { mutableStateOf(false) }
        var showRemoveDialog by remember { mutableStateOf(false) }

        if (showBottomSheet) {
            NewKhatmModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                viewModel.handleIntent(it)
                showBottomSheet = false
            }
        }

        LaunchedEffect(true) {
            viewModel.handleIntent(KhatmHomeIntent.LoadKhatmDetail)
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
                        AnimatedVisibility(showRemoveDialog) {
                            Dialog(
                                onDismissRequest = {
                                    showRemoveDialog = false
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clip(MaterialTheme.shapes.large)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "آیا مایل به حذف این ختم هستید ؟",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Start
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Row(

                                    ) {
                                        OutlinedButton(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                showRemoveDialog = false
                                            }
                                        ) {
                                            Text(
                                                text = "خیر",
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                showRemoveDialog = false
                                                viewModel.handleIntent(
                                                    KhatmHomeIntent.DeleteKhatm(
                                                        item
                                                    )
                                                )
                                            }
                                        ) {
                                            Text(
                                                text = "بله",
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(
                                    width = 1.dp,
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                .safeClickable {
                                    navController.navigate(Screen.KhatmDetail(item.id))
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                IconButton(
                                    modifier = Modifier.align(Alignment.TopStart),
                                    onClick = {
                                        showRemoveDialog = true
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = Icons.Outlined.RemoveCircleOutline,
                                        tint = Color.Red,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxWidth(0.6F)
                                        .padding(top = 16.dp, bottom = 8.dp)
                                        .basicMarquee(),
                                    text = item.name,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .align(Alignment.CenterEnd)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.tertiary,
                                            shape = MaterialTheme.shapes.large
                                        )
                                        .padding(all = 4.dp),
                                    text = when (item.type) {
                                        KhatmType.JOZ.name -> stringResource(R.string.label_raed_joz)
                                        KhatmType.HEZB.name -> stringResource(R.string.label_raed_hezb)
                                        KhatmType.PAGE.name -> stringResource(R.string.label_raed_page)
                                        else -> ""
                                    },
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall
                                )

                            }
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                progress = {
                                    item.completedPages.toFloat() / 604
                                },
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

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewKhatmModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmButton: (KhatmHomeIntent.NewKhatm) -> Unit
) {
    val bottomSheet = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = khatmName,
                onValueChange = { khatmName = it },
                label = { Text("عنوان ختم") },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .safeClickable { },
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
                    text = stringResource(R.string.label_raed_joz),
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
                    text = stringResource(R.string.label_raed_hezb),
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
                    text = stringResource(R.string.label_raed_page),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.weight(1F))

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