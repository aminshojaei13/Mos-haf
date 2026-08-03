package com.braveboy.mos_haf.presentation.feature.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.braveboy.mos_haf.BuildConfig
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.components.safeClickable
import com.braveboy.mos_haf.presentation.feature.detail.toPersianNumber
import com.braveboy.mos_haf.presentation.feature.khatm.khatmdetail.KhatmType
import com.braveboy.mos_haf.presentation.feature.khatm.model.KhatmVersesModel
import com.braveboy.mos_haf.presentation.navigation.Screen
import com.braveboy.mos_haf.presentation.navigation.Screen.KhatmVerses
import com.braveboy.mos_haf.presentation.navigation.Screen.QuranDetail
import com.braveboy.mos_haf.ui.theme.MoshafTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()
    val themeMode by viewModel.theme.collectAsState()
    val saveAudioSetting by viewModel.saveAudio.collectAsState()
    val scope = rememberCoroutineScope()

    var showSaveAudioDialog by remember { mutableStateOf(false) }

    LaunchedEffect(saveAudioSetting) {
        if (saveAudioSetting == null) {
            showSaveAudioDialog = true
        }
    }

    LaunchedEffect(true) {
        viewModel.getLastRead()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.label_top_app_bar),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            stringResource(R.string.label_top_app_bar2),
                            style = MaterialTheme.typography.bodySmall
                        )

                    }
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .safeClickable {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.saveTheme(!themeMode)
                                }
                            },
                        imageVector = if (themeMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = null
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val uriHandler = LocalUriHandler.current
        var showDialog by remember { mutableStateOf(false) }

        AnimatedVisibility(showSaveAudioDialog) {
            Dialog(
                onDismissRequest = {
                    showSaveAudioDialog = false
                    viewModel.saveAudioSetting(false)
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "مدیریت حافظه فایل‌های صوتی",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "برای راحتی شما، فایل‌های صوتی تلاوت‌های شنیده شده در حافظه موقت ذخیره می‌شوند. آیا مایلید این فایل‌ها برای استفاده آفلاین در آینده باقی بمانند؟ در صورت انتخاب «خیر»، فایل‌ها برای جلوگیری از پر شدن حافظه گوشی، به طور خودکار پاک می‌شوند.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                showSaveAudioDialog = false
                                viewModel.saveAudioSetting(false)
                            }
                        ) {
                            Text(
                                text = "خیر، حذف شود",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                showSaveAudioDialog = false
                                viewModel.saveAudioSetting(true)
                            }
                        ) {
                            Text(
                                text = "بله، ذخیره شود",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
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
                        text = "لطفا با ثبت امتیاز و نظر در بهبود و توسعه این برنامه مشارکت کنید.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "این برنامه در زمان جنگ رمضان توسعه داده شده است،",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(8.dp))


                    Text(
                        text = "برای شادی روح شهدا و گذشتگان صلوات.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "صوت برنامه به صورت آنلاین و از آدرس alquran.cloud دریافت می‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(4.dp))

                    val annotatedText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Gray,
                            )
                        ) {
                            append(
                                text = "متن و ترجمه قرآن، با استفاده از دیتابیس سایت ",
                            )
                        }

                        pushStringAnnotation(
                            tag = "URL",
                            annotation = "https://www.striing.ir/file/4:quran-database"
                        )
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF1E88E5), // رنگ آبی
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("ریسمان")
                        }
                        pop()

                        withStyle(
                            style = SpanStyle(
                                color = Color.Gray,
                            )
                        ) {
                            append(" می‌باشد.")
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ClickableText(
                            modifier = Modifier.align(Alignment.Center),
                            text = annotatedText,
                            style = MaterialTheme.typography.bodySmall,
                            onClick = { offset ->
                                annotatedText.getStringAnnotations(
                                    tag = "URL",
                                    start = offset,
                                    end = offset
                                )
                                    .firstOrNull()?.let { annotation ->
                                        uriHandler.openUri(annotation.item)
                                    }
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text(
                            text = "بستن",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            state.let { lastRead ->
                AnimatedVisibility(lastRead.start != null) {
                    LastReadCard(
                        suraName = lastRead.start?.suraName.orEmpty(),
                        ayaNumber = lastRead.start?.aya.toString().toPersianNumber()
                    ) {
                        state.let {
                            when (it.source) {
                                "detail" -> {
                                    navController.navigate(
                                        QuranDetail(
                                            it.start?.suraName.orEmpty(),
                                            true
                                        )
                                    )
                                }

                                "search" -> {
                                    navController.navigate(Screen.Search(true))
                                }

                                else -> {
                                    when (it.source) {
                                        KhatmType.JOZ.name -> {
                                            navController.navigate(
                                                KhatmVerses(
                                                    fromLast = true,
                                                    khatm = Json.encodeToString(
                                                        KhatmVersesModel(
                                                            id = it.id,
                                                            type = KhatmType.JOZ.name,
                                                            joz = it.start?.juz
                                                        )
                                                    )
                                                )
                                            )
                                        }

                                        KhatmType.HEZB.name -> {
                                            navController.navigate(
                                                KhatmVerses(
                                                    fromLast = true,
                                                    khatm = Json.encodeToString(
                                                        KhatmVersesModel(
                                                            id = it.id,
                                                            type = KhatmType.HEZB.name,
                                                            hezb = it.start?.hezb
                                                        )
                                                    )
                                                )
                                            )
                                        }

                                        KhatmType.PAGE.name -> {
                                            navController.navigate(
                                                KhatmVerses(
                                                    fromLast = true,
                                                    khatm = Json.encodeToString(
                                                        KhatmVersesModel(
                                                            id = it.id,
                                                            type = KhatmType.PAGE.name,
                                                            page = it.start?.page
                                                        )
                                                    )
                                                )
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            PopularSection { tile ->
                when (tile) {
                    Tile.QURAN -> {
                        navController.navigate(Screen.SuraList.route)
                    }

                    Tile.Search -> {
                        navController.navigate(Screen.Search(false))
                    }

                    Tile.KHATM -> {
                        navController.navigate(Screen.KhatmHome.route)
                    }

                    Tile.VOICE -> {

                    }
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1F))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "نسخه : " + BuildConfig.VERSION_NAME,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(8.dp))

                Icon(
                    modifier = Modifier
                        .size(18.dp)
                        .safeClickable {
                            showDialog = true
                        },
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun LastReadCard(
    suraName: String,
    ayaNumber: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.8f
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_last_read),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = suraName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "آیه شماره $ayaNumber",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(50),
                    onClick = {},
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .safeClickable { onClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.label_continue),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_history_book),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Composable
fun PopularSection(
    onClick: (Tile) -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PopularCard(
                modifier = Modifier
                    .weight(1f)
                    .safeClickable {
                        onClick(Tile.QURAN)
                    },
                title = stringResource(R.string.label_quran_tile),
                imageRes = R.drawable.ic_quran,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PopularCard(
                modifier = Modifier
                    .weight(1f)
                    .safeClickable {
                        onClick(Tile.Search)
                    },
                title = stringResource(R.string.label_search),
                imageRes = R.drawable.ic_mos_haf_search,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PopularCard(
                modifier = Modifier
                    .weight(1f)
                    .safeClickable {
                        onClick(Tile.KHATM)
                    },
                title = stringResource(R.string.label_khatm_quran),
                imageRes = R.drawable.ic_mos_haf_khatm,
            )
        }
    }
}

@Composable
fun PopularCard(
    modifier: Modifier = Modifier,
    title: String,
    imageRes: Int,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
    ) {
        if (title == "صوتی") {
            Badge(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("به‌زودی")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class Tile {
    QURAN,
    Search,
    VOICE,
    KHATM
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MoshafTheme(false) {
        HomeScreen(rememberNavController())
    }
}
