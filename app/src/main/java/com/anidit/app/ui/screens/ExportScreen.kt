package com.anidit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.StepHeader
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.theme.Cyan
import com.anidit.app.ui.theme.InkSurface
import com.anidit.app.ui.theme.TextMuted
import com.anidit.app.ui.theme.TextSecondary

private val PIPELINE_STEPS = listOf("Import", "Style", "Timeline", "Export")
private val RESOLUTIONS = listOf("720p", "1080p", "1440p", "4K")
private val FRAME_RATES = listOf("30 fps", "60 fps")

/**
 * Export options screen. Real implementation will query MediaCodecList for
 * supported encoders/resolutions on-device and hand off to a Media3
 * Transformer-based export pipeline. The Export button is currently a
 * no-op pending that engine.
 */
@Composable
fun ExportScreen(navController: NavHostController) {
    var resolution by remember { mutableStateOf("1080p") }
    var frameRate by remember { mutableStateOf("30 fps") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepHeader(steps = PIPELINE_STEPS, currentStepIndex = 3)
        Spacer(Modifier.height(20.dp))
        Text("Export", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "H.264/H.265 with GPU acceleration where available.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(20.dp))
        WaveformDivider(seed = 5)
        Spacer(Modifier.height(20.dp))

        Text("Resolution", style = MaterialTheme.typography.labelLarge, color = TextMuted)
        Spacer(Modifier.height(8.dp))
        ChoiceRow(options = RESOLUTIONS, selected = resolution, onSelect = { resolution = it })

        Spacer(Modifier.height(20.dp))
        Text("Frame rate", style = MaterialTheme.typography.labelLarge, color = TextMuted)
        Spacer(Modifier.height(8.dp))
        ChoiceRow(options = FRAME_RATES, selected = frameRate, onSelect = { frameRate = it })

        Spacer(Modifier.weight(1f))
        ImpactButton(text = "Export Edit", onClick = { /* TODO: export-engine handoff */ })
    }
}

@Composable
private fun ChoiceRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row {
        options.forEach { option ->
            val isSelected = option == selected
            Text(
                text = option,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color.Black else TextSecondary,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(if (isSelected) Cyan else InkSurface, RoundedCornerShape(14.dp))
                    .clickable { onSelect(option) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}
