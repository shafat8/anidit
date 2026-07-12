package com.anidit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anidit.app.state.ProjectState
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.nav.Routes
import com.anidit.app.ui.theme.Cyan
import com.anidit.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Cyan, modifier = Modifier.height(28.dp))
        Spacer(Modifier.height(16.dp))
        Text("AniDit", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Drop in clips and a song. Get a beat-synced edit back.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(Modifier.height(24.dp))
        WaveformDivider(seed = 1)
        Spacer(Modifier.height(32.dp))
        ImpactButton(
            text = "New Project",
            onClick = {
                ProjectState.reset()
                navController.navigate(Routes.IMPORT)
            }
        )
    }
}
