package com.braveboy.mos_haf.presentation.common_compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.domain.model.Quran
import com.braveboy.mos_haf.presentation.feature.detail.VerseItem
import com.braveboy.mos_haf.presentation.feature.detail.toPersianNumber
import ir.partsoftware.cup.common.compose.modifiers.safeClickable

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("FrequentlyChangingValue", "UnusedBoxWithConstraintsScope")
@Composable
fun AyatComponent(
    modifier: Modifier = Modifier,
    lazyState: LazyListState = rememberLazyListState(),
    verses: List<Quran>,
    translations: List<String>,
    fontSize: TextUnit,
    suras: List<String>? = null,
    overScrollEnable: Boolean = false,
    bookmarked: (Quran) -> Unit = {},
    changeSura: (String) -> Unit = {}
) {
    var showOtherSura by remember { mutableIntStateOf(0) }
    var bookmarkedAya by remember { mutableStateOf(false) }

    LaunchedEffect(bookmarkedAya) {
        if (bookmarkedAya) {
            val index =
                lazyState.firstVisibleItemIndex + lazyState.layoutInfo.visibleItemsInfo.lastIndex
            val verse = verses[index - 1]
            bookmarked(verse)
        }
    }

    LaunchedEffect(lazyState.isScrollInProgress) {
        if (overScrollEnable) {
            when {
                !lazyState.canScrollBackward && !lazyState.canScrollForward && lazyState.isScrollInProgress -> {
                    showOtherSura = 3
                }

                !lazyState.canScrollBackward && lazyState.isScrollInProgress -> {
                    showOtherSura = 1
                }

                !lazyState.canScrollForward && lazyState.isScrollInProgress -> {
                    showOtherSura = 2
                }

                lazyState.canScrollBackward && lazyState.canScrollForward -> {
                    showOtherSura = 0
                }
            }
        }
    }

    Box(
        modifier = modifier
            .padding(vertical = 16.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(topStartPercent = 8, topEndPercent = 8),
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        val visibleItemsInfo = lazyState.layoutInfo.visibleItemsInfo

        VerticalSlider(
            modifier = Modifier
                .padding(top = 28.dp)
                .padding(start = 4.dp)
                .fillMaxHeight(),
            value = if (visibleItemsInfo.isNotEmpty()) {
                visibleItemsInfo.last().index.toFloat() / verses.size
            } else 0f,
        )

        LazyColumn(
            modifier = Modifier
                .padding(16.dp),
            state = lazyState,
        ) {
            item {
                AnimatedVisibility(
                    visible = showOtherSura != 0 && showOtherSura != 2 && !suras.isNullOrEmpty(),
                    enter = slideInVertically()
                ) {
                    suras?.indexOf(verses.first().suraName)?.let {
                        if (it > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .safeClickable {
                                        changeSura(suras[it - 1])
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowCircleUp,
                                    contentDescription = "",
                                    tint = Color.Yellow
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = suras[it - 1],
                                    color = Color.Yellow
                                )
                            }
                        }
                    }
                }
            }

            itemsIndexed(verses) { index, verse ->
                if (verse.aya == 1 || index == 0) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = verse.suraName.orEmpty(),
                            modifier = Modifier.weight(.3f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (verse.suraName != "التوبة") {
                            Text(
                                text = stringResource(R.string.label_bismillah),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "جز " + verses.first().juz.toString().toPersianNumber(),
                            modifier = Modifier.weight(.3f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                if (verse.text.isNotBlank()) {
                    VerseItem(
                        arabicText = verse.text,
                        translationText = translations[index],
                        ayaNumber = verse.aya.toString(),
                        fontSize = fontSize,
                        icon = null,
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = showOtherSura != 0 && showOtherSura != 1,
                    enter = slideInVertically()
                ) {
                    suras?.indexOf(verses.first().suraName)?.let {
                        if (it < suras.lastIndex) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth()
                                    .safeClickable {
                                        changeSura(suras[it + 1])
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowCircleDown,
                                    contentDescription = "",
                                    tint = Color.Yellow
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = suras[it + 1],
                                    color = Color.Yellow
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = {
                bookmarkedAya = !bookmarkedAya
            }
        ) {
            AnimatedVisibility(
                visible = bookmarkedAya,
                enter = slideInVertically(),
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null)
            }
            AnimatedVisibility(
                visible = !bookmarkedAya,
                enter = slideInVertically(),
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null)
            }
        }
    }

}
