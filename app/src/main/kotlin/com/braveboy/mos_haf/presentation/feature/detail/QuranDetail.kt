package com.braveboy.mos_haf.presentation.feature.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.presentation.common_compose.AyatComponent
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import ir.partsoftware.cup.common.compose.modifiers.safeClickable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailScreen(
    navController: NavController,
    viewModel: QuranDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var expandedFontSize by remember { mutableStateOf(false) }
    val sliderState = rememberSliderState(value = 0.5f, steps = 5)
    var fontSize by remember {
        mutableStateOf(28.sp)
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
                            onDismissRequest = { expandedFontSize = false },
                        ) {
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            AyatComponent(
                modifier = Modifier.padding(paddingValues),
                verses = state.verses,
                translations = state.translations,
                fontSize = fontSize,
                suras = state.suraNames,
                overScrollEnable = true,
                changeSura = {
                    viewModel.loadVersesAndTranslations(
                        it
                    )
                }
            )
        }
    }
}

@Composable
fun VerseItem(
    arabicText: String,
    translationText: String,
    ayaNumber: String,
    fontSize: TextUnit,
    icon: Int? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
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
                    color = MaterialTheme.colorScheme.onBackground,
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuranDetailScreenPreview() {
    MoshafTheme {
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
