package com.braveboy.mos_haf.presentation.feature.khatm.newkhatm

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.data.local.entity.KhatmEntity
import com.braveboy.mos_haf.domain.model.LastReadModel
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.presentation.feature.detail.toPersianWord
import com.braveboy.mos_haf.presentation.feature.search.AyaDropdown
import com.braveboy.mos_haf.presentation.feature.search.JozOrHezbDropdown
import com.braveboy.mos_haf.presentation.feature.search.SuraDropdown
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewKhatmScreen(navController: NavController) {
    val viewModel = koinViewModel<NewKhatmViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_new_khatm),
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
        var khatmName by remember { mutableStateOf("") }
        var selectedKhatmType by remember { mutableStateOf(KhatmType.JOZ) }
        var startSuraIndex by remember { mutableStateOf<Int?>(null) }
        var endSuraIndex by remember { mutableStateOf<Int?>(null) }
        var startAyaIndex by remember { mutableStateOf<Int?>(null) }
        var endAyaIndex by remember { mutableStateOf<Int?>(null) }
        var jozIndex by remember { mutableStateOf<Int?>(null) }
        var hezbIndex by remember { mutableStateOf<Int?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TextField(
                value = khatmName,
                onValueChange = { khatmName = it },
                label = { Text("عنوان ختم") },
                readOnly = false,
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /* AnimatedVisibility(selectedSuraName.isNotEmpty()) {
                             Icon(
                                 imageVector = Icons.Default.Clear,
                                 contentDescription = null,
                                 modifier = Modifier
                                     .size(16.dp)
                                     .safeClickable { selectedSuraName = "" }
                             )
                         }*/
                    }
                },
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
                )
            )

            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "نحوه ختم قرآن :",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
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

            Spacer(Modifier.height(16.dp))

            when (selectedKhatmType) {
                KhatmType.JOZ -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        JozOrHezbDropdown(
                            modifier = Modifier.fillMaxWidth(),
                            label = "جز",
                            selectedIndex = jozIndex,
                            onSelected = { jozIndex = it }
                        )
                    }
                }

                KhatmType.HEZB -> {
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
                    }
                }

                KhatmType.RANGE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuraDropdown(
                            label = stringResource(R.string.label_from_surah),
                            suraNames = state.value.suraNames,
                            selectedSuraIndex = startSuraIndex,
                            onSuraSelected = { sura ->
                                startSuraIndex =
                                    state.value.suraNames.map { it.toPersianWord() }.indexOf(sura)
                                startAyaIndex = 0
                            },
                            modifier = Modifier.weight(1f)
                        )

                        AyaDropdown(
                            label = stringResource(R.string.label_aya),
                            ayaCount = startSuraIndex?.let { state.value.ayaCounts.getOrNull(it) }
                                ?: 0,
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
                            suraNames = state.value.suraNames,
                            selectedSuraIndex = endSuraIndex,
                            onSuraSelected = { sura ->
                                Log.d("xavi", sura)
                                endSuraIndex =
                                    state.value.suraNames.map { it.toPersianWord() }.indexOf(sura)
                                endAyaIndex = 0
                            },
                            modifier = Modifier.weight(1f)
                        )

                        AyaDropdown(
                            label = "آیه",
                            ayaCount = endSuraIndex?.let { state.value.ayaCounts.getOrNull(it) }
                                ?: 0,
                            selectedAyaIndex = endAyaIndex,
                            onAyaSelected = { endAyaIndex = it },
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }

                else -> {}
            }

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    /*when (selectedKhatmType) {
                        KhatmType.JOZ -> {
                            viewModel.createNewKhatm(
                                KhatmEntity(
                                    name = khatmName,
                                    type = KhatmType.JOZ.name,
                                    lastRead = LastReadModel(
                                        source = null,
                                        start = null,
                                        end = null
                                    )
                                )
                            )
                        }

                        KhatmType.HEZB -> {
                            viewModel.createNewKhatm(
                                KhatmEntity(
                                    name = khatmName,
                                    type = KhatmType.HEZB.name,
                                    lastRead = LastReadModel(
                                        source = null,
                                        start = null,
                                        end = null
                                    )
                                )
                            )
                        }
                        KhatmType.RANGE -> {
                            val sSura = startSuraIndex?.plus(1)
                            val eSura = endSuraIndex?.plus(1)
                            val sAya = startAyaIndex?.plus(1)
                            val eAya = endAyaIndex?.plus(1)

                            viewModel.createNewKhatm(
                                KhatmEntity(
                                    name = khatmName,
                                    type = KhatmType.JOZ.name,
                                    lastRead = LastReadModel(
                                        source = "khatm",
                                        start = Quran(
                                            id = TODO(),
                                            sura = TODO(),
                                            aya = TODO(),
                                            text = TODO(),
                                            suraName = TODO(),
                                            page = TODO(),
                                            juz = TODO(),
                                            hezb = TODO()
                                        ),
                                        end = null
                                    )
                                )
                            )
                            *//*SearchIntent.LoadVersesByDetailedRange(
                                sSura, sAya, eSura, eAya
                            )*//*
                        }
                    }*/
                },
                enabled = true
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ایجاد و شروع ختم",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

    }

}

enum class KhatmType {
    JOZ,
    HEZB,
    PAGE,
    RANGE
}