package com.anidit.app.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anidit.app.state.ProjectState
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.StepHeader
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.nav.Routes
import com.anidit.app.ui.theme.Cyan
import com.anidit.app.ui.theme.InkSurface
import com.anidit.app.ui.theme.TextMuted
import com.anidit.app.ui.theme.TextSecondary

private val PIPELINE_STEPS = listOf("Import", "Style", "Timeline", "Export")

@Composable
fun ImportMediaScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Read initial values from shared state so returning to this screen
    // (e.g. via back navigation) doesn't lose a prior selection.
    var clipCount by remember { mutableStateOf(ProjectState.selectedClips.size) }
    var songName by remember { mutableStateOf(ProjectState.selectedSong.value?.lastPathSegment) }

    // ACTION_GET_CONTENT with allowMultiple, filtered to video mime types.
    // This launches the system's own media/file picker UI - no runtime
    // permission needed, the picker itself grants scoped access to
    // whatever the user selects.
    val clipPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isEmpty()) {
            Toast.makeText(context, "No clips selected", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        ProjectState.selectedClips.clear()
        ProjectState.selectedClips.addAll(uris)
        clipCount = uris.size
        Toast.makeText(context, "$clipCount clip(s) added", Toast.LENGTH_SHORT).show()
    }

    // ACTION_OPEN_DOCUMENT filtered to audio mime types.
    val songPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(context, "No song selected", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        ProjectState.selectedSong.value = uri
        songName = uri.lastPathSegment
        Toast.makeText(context, "Song added", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        StepHeader(steps = PIPELINE_STEPS, currentStepIndex = 0)
        Spacer(Modifier.height(20.dp))
        Text("Import your media", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Pick one or more anime clips and a track to cut them to.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(20.dp))
        WaveformDivider(seed = 2)
        Spacer(Modifier.height(20.dp))

        MediaPickerRow(
            icon = Icons.Filled.VideoLibrary,
            title = "Anime Clips",
            subtitle = if (clipCount > 0) "$clipCount clip${if (clipCount == 1) "" else "s"} selected" else "Tap to choose clips",
            done = clipCount > 0,
            onClick = {
                // MIME wildcard "video/*" scopes the system picker to video
                // content only.
                clipPicker.launch("video/*")
            }
        )
        Spacer(Modifier.height(12.dp))
        MediaPickerRow(
            icon = Icons.Filled.MusicNote,
            title = "Song",
            subtitle = songName ?: "Tap to choose a song",
            done = songName != null,
            onClick = {
                songPicker.launch(arrayOf("audio/*"))
            }
        )

        Spacer(Modifier.height(28.dp))
        ImpactButton(
            text = "Continue",
            enabled = clipCount > 0 && songName != null,
            onClick = { navController.navigate(Routes.PROMPT_GENERATE) }
        )
    }
}

@Composable
private fun MediaPickerRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    done: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InkSurface, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (done) Cyan else TextMuted,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp).size(24.dp)
        )
        Column(
            modifier = Modifier.padding(start = 12.dp, top = 14.dp, bottom = 14.dp).weight(1f)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        if (done) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.padding(end = 16.dp).size(20.dp)
            )
        }
    }
}
