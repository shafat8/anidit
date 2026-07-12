package com.anidit.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.anidit.app.ui.theme.InkLine
import kotlin.random.Random

@Composable
fun WaveformDivider(modifier: Modifier = Modifier, seed: Int = 0, barCount: Int = 48) {
    val heights = remember(seed, barCount) {
        val rnd = Random(seed)
        List(barCount) { i ->
            val envelope = kotlin.math.sin((i / barCount.toFloat()) * Math.PI).toFloat()
            0.15f + 0.65f * envelope * rnd.nextFloat().coerceIn(0.3f, 1f)
        }
    }

    Canvas(modifier = modifier.fillMaxWidth().height(20.dp)) {
        val barWidth = size.width / barCount
        val midY = size.height / 2f
        heights.forEachIndexed { i, h ->
            val x = i * barWidth + barWidth / 2f
            val halfBar = (size.height / 2f) * h
            drawLine(
                color = InkLine,
                start = Offset(x, midY - halfBar),
                end = Offset(x, midY + halfBar),
                strokeWidth = (barWidth * 0.5f).coerceAtMost(3.dp.toPx()),
                cap = StrokeCap.Round
            )
        }
    }
}
