package com.anidit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anidit.app.ui.theme.ImpactGradient
import com.anidit.app.ui.theme.InkSurfaceAlt
import com.anidit.app.ui.theme.TextMuted

@Composable
fun StepHeader(steps: List<String>, currentStepIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, _ ->
            val isDone = index < currentStepIndex
            val isCurrent = index == currentStepIndex

            val circleModifier = if (isDone || isCurrent) {
                Modifier.background(ImpactGradient, CircleShape)
            } else {
                Modifier.background(InkSurfaceAlt, CircleShape)
            }
            Box(
                modifier = Modifier.size(26.dp).then(circleModifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDone || isCurrent) Color.White else TextMuted
                )
            }
            if (index != steps.lastIndex) {
                val lineModifier = if (isDone) {
                    Modifier.background(ImpactGradient)
                } else {
                    Modifier.background(InkSurfaceAlt)
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(width = 14.dp, height = 2.dp)
                        .then(lineModifier)
                )
            }
        }
    }
}
