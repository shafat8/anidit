package com.anidit.app.export

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File

/**
 * Real (not simulated) video export using Media3's Transformer API.
 *
 * What this actually does today:
 *  - Splits the song's target duration evenly across the selected clips.
 *  - Clips each source video to that even segment (from its start), strips
 *    its original audio.
 *  - Lays the selected song underneath the concatenated clips, trimmed to
 *    the same total duration.
 *  - Encodes a real .mp4 file via hardware-accelerated MediaCodec where the
 *    device supports it (Transformer handles this automatically).
 *
 * What this does NOT do yet (see ARCHITECTURE.md):
 *  - No beat detection — cuts are evenly spaced, not synced to the music.
 *  - No scene analysis — always cuts from the start of each clip.
 *  - No effects (flashes, shake, color grade, etc).
 * Those require the audio-analysis / video-analysis / decision-engine /
 * effects modules to be built out. This exporter is intentionally the
 * simplest possible real pipeline so "Export" produces an actual file
 * while that work continues.
 */
object VideoExporter {

    sealed class ExportState {
        data object Idle : ExportState()
        data class InProgress(val progress: Float) : ExportState()
        data class Success(val file: File) : ExportState()
        data class Failed(val message: String) : ExportState()
    }

    /**
     * Kicks off an export. Callbacks fire on the main thread (Transformer's
     * default behavior when constructed on the main thread).
     *
     * [songDurationMs] should be the actual song length if known; if 0,
     * this falls back to a fixed default so export still works even before
     * real audio metadata reading exists.
     */
    fun export(
        context: Context,
        clipUris: List<Uri>,
        songUri: Uri,
        songDurationMs: Long,
        onStateChange: (ExportState) -> Unit
    ) {
        if (clipUris.isEmpty()) {
            onStateChange(ExportState.Failed("No clips selected"))
            return
        }

        val totalDurationMs = if (songDurationMs > 0) songDurationMs else DEFAULT_TOTAL_DURATION_MS
        val perClipMs = (totalDurationMs / clipUris.size).coerceAtLeast(MIN_SEGMENT_MS)

        val videoEditedItems = clipUris.map { uri ->
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(0)
                        .setEndPositionMs(perClipMs)
                        .build()
                )
                .build()
            EditedMediaItem.Builder(mediaItem)
                .setRemoveAudio(true)
                .build()
        }
        val videoSequence = EditedMediaItemSequence(videoEditedItems)

        val audioMediaItem = MediaItem.Builder()
            .setUri(songUri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(0)
                    .setEndPositionMs(perClipMs * clipUris.size)
                    .build()
            )
            .build()
        val audioEditedItem = EditedMediaItem.Builder(audioMediaItem)
            .setRemoveVideo(true)
            .build()
        val audioSequence = EditedMediaItemSequence(listOf(audioEditedItem))

        val composition = Composition.Builder(listOf(videoSequence, audioSequence)).build()

        val outputDir = File(context.getExternalFilesDir(null), "exports").apply { mkdirs() }
        val outputFile = File(outputDir, "anidit_export_${System.currentTimeMillis()}.mp4")

        val transformer = Transformer.Builder(context)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    onStateChange(ExportState.Success(outputFile))
                }

                override fun onError(
                    composition: Composition,
                    exportResult: ExportResult,
                    exportException: ExportException
                ) {
                    onStateChange(
                        ExportState.Failed(exportException.message ?: "Export failed")
                    )
                }
            })
            .build()

        onStateChange(ExportState.InProgress(0f))
        transformer.start(composition, outputFile.absolutePath)
    }

    private const val DEFAULT_TOTAL_DURATION_MS = 15_000L
    private const val MIN_SEGMENT_MS = 500L
}
