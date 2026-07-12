package com.anidit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anidit.app.ui.theme.ImpactGradient
import com.anidit.app.ui.theme.InkSurfaceAlt
import com.anidit.app.ui.theme.TextMuted

@Composable
fun ImpactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(brush = if (enabled) ImpactGradient else null, color = if (!enabled) InkSurfaceAlt else Color.Transparent, shape = RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) Color.White else TextMuted
        )
    }
}

private fun Modifier.background(brush: Brush?, color: Color, shape: RoundedCornerShape): Modifier =
    if (brush != null) this.background(brush = brush, shape = shape) else this.background(color = color, shape = shape)
