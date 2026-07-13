package com.anidit.app.ui.screens

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.navigation.NavHostController
import com.anidit.app.engine.SimpleEditPlanner
import com.anidit.app.state.ProjectState
import com.anidit.app.ui.components.ImpactButton
import com.anidit.app.ui.components.StepHeader
import com.anidit.app.ui.components.WaveformDivider
import com.anidit.app.ui.theme.Cyan
import com.anidit.app.ui.theme.InkSurface
import com.anidit.app.ui.theme.TextMuted
import com.anidit.app.ui.theme.TextSecondary
import java.io.File

private val PIPELINE_STEPS = listOf("Import", "Style", "Timeline", "Export")
private val RESOLUTIONS = listOf("720p", "1080p", "1440p", "4K")
private val FRAME_RATES = listOf("30 fps", "60 fps")

/**
 * Real export screen: renders the same equal-interval cut plan used in
 * Preview into an actual mp4 file via Media3 Transformer, muting each
 * clip's own audio and replacing it with the selected song.
 *
 * Resolution/frame-rate pickers are currently display-only - Transformer's
 * output currently follows the source clips' native format. Wiring these
 * controls to an actual output VideoFormat is a follow-up, not done here
 * to keep this pass focused on "does export produce a real file at all."
 */
@Composable
fun ExportScreen(navController: NavHostController) {
    val context = LocalContext.current
    var resolution by remember { mutableStateOf("1080p") }
    var frameRate by remember { mutableStateOf("30 fps") }
    var status by remember { mutableStateOf<String?>(null) }
    var isExporting by remember { mutableStateOf(false) }

    val hasSelection = ProjectState.selectedClips.isNotEmpty() && ProjectState.selectedSong.value != null

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepHeader(steps = PIPELINE_STEPS, currentStepIndex = 3)
        Spacer(Modifier.height(20.dp))
        Text("Export", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Renders your clips (muted) over the selected song.",
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

        Spacer(Modifier.height(20.dp))
        status?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = Cyan)
        }

        Spacer(Modifier.weight(1f))

        if (isExporting) {
            CircularProgressIndicator(
                color = Cyan,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))
        }

        ImpactButton(
            text = if (isExporting) "Exporting…" else "Export Edit",
            enabled = !isExporting && hasSelection,
            onClick = {
                val songUri = ProjectState.selectedSong.value
                if (songUri == null) {
                    status = "Select a song first"
                    return@ImpactButton
                }
                isExporting = true
                status = "Preparing…"
                runExport(
                    context = context,
                    clipUris = ProjectState.selectedClips.toList(),
                    songUri = songUri,
                    onProgress = { status = it },
                    onDone = { path ->
                        isExporting = false
                        status = "Saved: $path"
                    },
                    onError = { message ->
                        isExporting = false
                        status = "Export failed: $message"
                    }
                )
            }
        )
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

/**
 * Kicks off a real Media3 Transformer export: a muted video sequence built
 * from [SimpleEditPlanner]'s cut plan, composited with the song as a
 * separate audio sequence. Writes to app-specific external storage (no
 * runtime permission needed on any supported API level), then best-effort
 * copies into the public Movies collection on API 29+ so it shows up in
 * the phone's gallery/file manager too.
 *
 * This is the single riskiest piece of code in the app right now: it's
 * built carefully against the Media3 1.3.1 Transformer/Composition API as
 * documented, but hasn't been compiled in this environment. If the GitHub
 * Actions build fails here, that means a real API mismatch to fix, not a
 * placeholder pretending to work.
 */
private fun runExport(
    context: Context,
    clipUris: List<Uri>,
    songUri: Uri,
    onProgress: (String) -> Unit,
    onDone: (String) -> Unit,
    onError: (String) -> Unit
) {
    Thread {
        try {
            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            if (outputDir == null) {
                onError("No storage available")
                return@Thread
            }
            if (!outputDir.exists()) outputDir.mkdirs()
            val outputFile = File(outputDir, "anidit_export_${System.currentTimeMillis()}.mp4")

            val songDurationMs = SimpleEditPlanner.durationMs(context, songUri)
            val cutPlan = SimpleEditPlanner.buildEqualCutPlan(context, clipUris, songDurationMs)
            if (cutPlan.isEmpty()) {
                onError("No usable clips or song duration")
                return@Thread
            }
            val totalVideoMs = SimpleEditPlanner.totalDurationMs(cutPlan)

            val videoItems = cutPlan.map { cut ->
                EditedMediaItem.Builder(
                    MediaItem.Builder()
                        .setUri(cut.uri)
                        .setClippingConfiguration(
                            MediaItem.ClippingConfiguration.Builder()
                                .setStartPositionMs(cut.startMs)
                                .setEndPositionMs(cut.endMs)
                                .build()
                        )
                        .build()
                ).setRemoveAudio(true).build()
            }
            val videoSequence = EditedMediaItemSequence(videoItems)

            // Clip the song to match the total video duration so the
            // export doesn't run audio-only past the end of the visuals.
            val audioEndMs = totalVideoMs.coerceAtMost(songDurationMs).coerceAtLeast(200L)
            val audioItem = EditedMediaItem.Builder(
                MediaItem.Builder()
                    .setUri(songUri)
                    .setClippingConfiguration(
                        MediaItem.ClippingConfiguration.Builder()
                            .setStartPositionMs(0L)
                            .setEndPositionMs(audioEndMs)
                            .build()
                    )
                    .build()
            ).build()
            val audioSequence = EditedMediaItemSequence(listOf(audioItem))

            val composition = Composition.Builder(listOf(videoSequence, audioSequence)).build()

            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onProgress("Rendering…")

                val transformer = Transformer.Builder(context)
                    .addListener(object : Transformer.Listener {
                        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                            Thread {
                                copyToGalleryIfPossible(context, outputFile)
                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    onDone(outputFile.absolutePath)
                                }
                            }.start()
                        }

                        override fun onError(
                            composition: Composition,
                            exportResult: ExportResult,
                            exportException: ExportException
                        ) {
                            onError(exportException.message ?: "unknown export error")
                        }
                    })
                    .build()

                transformer.start(composition, outputFile.absolutePath)
            }
        } catch (e: Exception) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                onError(e.message ?: e.javaClass.simpleName)
            }
        }
    }.start()
}

/**
 * Best-effort copy into the public Movies collection (API 29+) so the
 * export is visible in the phone's gallery/file manager, not just app-
 * private storage. No-ops below API 29 to avoid needing legacy storage
 * permissions for this pass - the file is still saved either way.
 */
private fun copyToGalleryIfPossible(context: Context, file: File) {
    if (Build.VERSION.SDK_INT < 29) return
    try {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/AniDit")
        }
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values) ?: return
        resolver.openOutputStream(uri)?.use { out ->
            file.inputStream().use { input -> input.copyTo(out) }
        }
    } catch (_: Exception) {
        // Non-fatal - the file still exists in app-specific storage.
    }
}
