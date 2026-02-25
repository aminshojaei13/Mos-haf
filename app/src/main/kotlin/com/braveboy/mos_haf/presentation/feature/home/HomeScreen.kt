package com.braveboy.mos_haf.presentation.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.braveboy.mos_haf.R
import com.braveboy.mos_haf.presentation.navigation.Screen
import com.braveboy.mos_haf.ui.theme.MoshafTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_top_app_bar),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LastReadCard()

            Spacer(modifier = Modifier.height(40.dp))

            PopularSection { tile ->
                when{
                    tile == Tile.QURAN -> {
                        navController.navigate(Screen.SuraList.route)
                    }
                    tile == Tile.KHATM -> {
                        navController.navigate(Screen.Search.route)
                    }
                    tile == Tile.VOICE -> {

                    }
                }

            }
        }
    }
}

@Composable
fun LastReadCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
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
                    text = "فاتحه",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "آیه شماره ۱",
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
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
    onClick : (Tile) -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PopularCard(
                modifier = Modifier.weight(1f).clickable{
                    onClick(Tile.QURAN)
                },
                title = stringResource(R.string.label_quran_tile),
                imageRes = R.drawable.ic_quran,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PopularCard(
                modifier = Modifier.weight(1f).clickable{
                    onClick(Tile.KHATM)
                },
                title = stringResource(R.string.label_khatm_quran),
                imageRes = R.drawable.ic_khatm_quran,
            )

            PopularCard(
                modifier = Modifier.weight(1f).clickable{
                    onClick(Tile.VOICE)
                },
                title = stringResource(R.string.label_quran_voice),
                imageRes = R.drawable.ic_listening,
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
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

enum class Tile {
    QURAN,
    KHATM,
    VOICE,
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MoshafTheme {
        HomeScreen(rememberNavController())
    }
}
