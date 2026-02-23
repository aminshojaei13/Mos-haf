package com.braveboy.mos_haf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.braveboy.mos_haf.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoshafTopAppBar() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        text = stringResource(R.string.label_top_app_bar),
        textAlign = TextAlign.Center
    )
}
