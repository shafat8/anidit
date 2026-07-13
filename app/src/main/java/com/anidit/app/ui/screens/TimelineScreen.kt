package com.anidit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.anidit.app.engine.ClipCut
import com.anidit.app.engine.SimpleEditPlanner
import com.anidit.app.state.ProjectState
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.StepHeader
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.nav.Routes
import com.anidit.app.ui.theme.InkSurface
import com.anidit.app.ui.theme.TextMuted
import com.anidit.app.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val PIPELINE_STEPS = listOf("Import", "Style", "Timeline", "Export")

/**
 * Real preview: plays the clip sequence produced by [SimpleEditPlanner]
 * (muted) alongside the selected song (as a second, independently-driven
 * player started at the same instant). This is a genuine, working preview
 * of the edit, not a placeholder box - the tradeoff versus the eventual
 * real renderer is that video/audio sync here is "started together" rather
 * than sample-accurate, and cuts are equal-interval rather than beat-synced
 * (see SimpleEditPlanner's doc comment).
 */
@Composable
fun TimelineScreen(navController: NavHostController) {
    val context = LocalContext.current

    var cutPlan by remember { mutableStateOf<List<ClipCut>>(emptyList()) }
    var isPlanning by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(false) }

    val videoPlayer = remember {
        ExoPlayer.Builder(context).build().apply { volume = 0f }
    }
    val audioPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        onDispose {
            videoPlayer.release()
            audioPlayer.release()
        }
    }

    // Compute the cut plan once when the screen is entered (or if the
    // underlying selection somehow changes while it's open).
    LaunchedEffect(ProjectState.selectedClips.size, ProjectState.selectedSong.value) {
        isPlanning = true
        val songUri = ProjectState.selectedSong.value
        val clips = ProjectState.selectedClips.toList()
        if (songUri != null && clips.isNotEmpty()) {
            val songDurationMs = withContext(Dispatchers.IO) {
                SimpleEditPlanner.durationMs(context, songUri)
            }
            cutPlan = withContext(Dispatchers.IO) {
                SimpleEditPlanner.buildEqualCutPlan(context, clips, songDurationMs)
            }
        } else {
            cutPlan = emptyList()
        }
        isPlanning = false
    }

    // Wire the cut plan into both players once it's ready.
    LaunchedEffect(cutPlan) {
        if (cutPlan.isEmpty()) return@LaunchedEffect

        val videoItems = cutPlan.map { cut ->
            MediaItem.Builder()
                .setUri(cut.uri)
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(cut.startMs)
                        .setEndPositionMs(cut.endMs)
                        .build()
                )
                .build()
        }
        videoPlayer.setMediaItems(videoItems)
        videoPlayer.prepare()

        val songUri = ProjectState.selectedSong.value
        if (songUri != null) {
            audioPlayer.setMediaItem(MediaItem.fromUri(songUri))
            audioPlayer.prepare()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepHeader(steps = PIPELINE_STEPS, currentStepIndex = 2)
        Spacer(Modifier.height(20.dp))
        Text("Preview", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            if (cutPlan.isNotEmpty()) {
                "${cutPlan.size} clip(s) · equal-interval cut synced to song length"
            } else {
                "Import clips and a song to preview a cut"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .background(InkSurface, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                isPlanning -> {
                    CircularProgressIndicator(color = TextMuted)
                }
                cutPlan.isEmpty() -> {
                    Text(
                        "Nothing to preview yet",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = videoPlayer
                                useController = false
                            }
                        }
                    )
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .clickable {
                                if (isPlaying) {
                                    videoPlayer.pause()
                                    audioPlayer.pause()
                                } else {
                                    videoPlayer.seekTo(0, 0)
                                    audioPlayer.seekTo(0)
                                    videoPlayer.play()
                                    audioPlayer.play()
                                }
                                isPlaying = !isPlaying
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        WaveformDivider(seed = 4)
        Spacer(Modifier.height(12.dp))
        Text(
            "Equal-interval cut for now - real beat sync is the next build step.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.weight(1f))
        ImpactButton(
            text = "Continue to Export",
            enabled = cutPlan.isNotEmpty(),
            onClick = {
                videoPlayer.pause()
                audioPlayer.pause()
                navController.navigate(Routes.EXPORT)
            }
        )
    }
}
