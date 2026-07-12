package com.anidit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anidit.app.state.ProjectState
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.StepHeader
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.nav.Routes
import com.anidit.app.ui.theme.Cyan
import com.anidit.app.ui.theme.InkSurface
import com.anidit.app.ui.theme.InkSurfaceAlt
import com.anidit.app.ui.theme.TextMuted
import com.anidit.app.ui.theme.TextSecondary
import com.anidit.core.StylePresets

private val PIPELINE_STEPS = listOf("Import", "Style", "Timeline", "Export")

@Composable
fun PromptGenerateScreen(navController: NavHostController) {
    var prompt by remember { mutableStateOf(ProjectState.prompt.value) }
    var selectedPreset by remember { mutableStateOf(ProjectState.selectedPresetName.value) }
    val presets = remember { StylePresets.all() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepHeader(steps = PIPELINE_STEPS, currentStepIndex = 1)
        Spacer(Modifier.height(20.dp))
        Text("Describe the edit", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Optional — pacing and effects are inferred from this and the preset you pick.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(20.dp))
        WaveformDivider(seed = 3)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = {
                prompt = it
                ProjectState.prompt.value = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("\"Fast-paced AMV with heavy beat sync\"", color = TextMuted) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = InkSurface,
                focusedContainerColor = InkSurface,
                unfocusedBorderColor = InkSurfaceAlt,
                focusedBorderColor = Cyan
            ),
            minLines = 2
        )

        Spacer(Modifier.height(24.dp))
        Text("Or pick a preset", style = MaterialTheme.typography.labelLarge, color = TextMuted)
        Spacer(Modifier.height(10.dp))
        LazyRow {
            items(presets) { preset ->
                val isSelected = selectedPreset == preset.name
                Row(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .background(if (isSelected) Cyan else InkSurface, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedPreset = if (isSelected) null else preset.name
                            ProjectState.selectedPresetName.value = selectedPreset
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        preset.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        ImpactButton(text = "Generate Edit", onClick = { navController.navigate(Routes.TIMELINE) })
    }
}
