package com.braveboy.mos_haf.presentation.common_compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VerticalSlider(
    value: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(4.dp)
            .background(
                color = Color.LightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .rotate(-180f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height((value.coerceIn(0f, 1f) * maxHeight.value).dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp)
                )
        )
    }
}