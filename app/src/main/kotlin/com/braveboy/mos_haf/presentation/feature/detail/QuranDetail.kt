package com.braveboy.mos_haf.presentation.feature.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("الفاتحة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back navigation */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle more options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32), // A green shade
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            // Placeholder for 7 verses of Al-Fatiha
            item {
                VerseItem(
                    arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    translationText = "[All] praise is [due] to Allah, Lord of the worlds"
                )
            }
            item {
                VerseItem(
                    arabicText = "الرَّحْمَٰنِ الرَّحِيمِ",
                    translationText = "The Entirely Merciful, the Especially Merciful,"
                )
            }
            item {
                VerseItem(
                    arabicText = "مَالِكِ يَوْمِ الدِّينِ",
                    translationText = "Sovereign of the Day of Recompense."
                )
            }
            item {
                VerseItem(
                    arabicText = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                    translationText = "It is You we worship and You we ask for help."
                )
            }
            item {
                VerseItem(
                    arabicText = "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                    translationText = "Guide us to the straight path -"
                )
            }
            item {
                VerseItem(
                    arabicText = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                    translationText = "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray."
                )
            }
        }
    }
}

@Composable
fun VerseItem(arabicText: String, translationText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = arabicText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = translationText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuranDetailScreenPreview() {
    QuranDetailScreen()
}
