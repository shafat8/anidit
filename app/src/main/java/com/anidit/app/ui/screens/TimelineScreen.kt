package com.anidit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anidit.app.state.ProjectState
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.StepHeader
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.nav.Routes
import com.anidit.app.ui.theme.InkSurface
import com.anidit.app.ui.theme.TextMuted
import com.anidit.app.ui.theme.TextSecondary

private val PIPELINE_STEPS = listOf("Import", "Style", "Timeline", "Export")

/**
 * Placeholder preview surface until the real renderer (ExoPlayer + effects
 * compositor, fed by the decision-engine's EditPlan) is built.
 */
@Composable
fun TimelineScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepHeader(steps = PIPELINE_STEPS, currentStepIndex = 2)
        Spacer(Modifier.height(20.dp))
        Text("Preview", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "${ProjectState.selectedClips.size} clip(s) · beat-synced cuts pending render",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(9f / 16f).background(InkSurface, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Preview", tint = TextMuted, modifier = Modifier.height(48.dp))
        }

        Spacer(Modifier.height(20.dp))
        WaveformDivider(seed = 4)
        Spacer(Modifier.height(12.dp))
        Text(
            "Clip track and manual trim/reorder controls go here.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.weight(1f))
        ImpactButton(text = "Continue to Export", onClick = { navController.navigate(Routes.EXPORT) })
    }
}
