package com.anidit.app.engine

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

/**
 * A single trimmed clip in the edit: play [uri] from [startMs] to [endMs].
 */
data class ClipCut(val uri: Uri, val startMs: Long, val endMs: Long)

/**
 * A naive, real, working stand-in for the beat-synced decision engine
 * described in ARCHITECTURE.md. It does not analyze audio or video at all
 * - it just divides the song's total duration evenly across the selected
 * clips and trims each clip to that slice. This produces an actual cut
 * video today instead of nothing, while the real beat/scene-aware engine
 * is still being built.
 */
object SimpleEditPlanner {

    /**
     * Reads the duration of a media file at [uri] in milliseconds.
     * Returns 0 if the duration can't be read (e.g. unsupported format).
     * Must be called off the main thread - MediaMetadataRetriever performs
     * blocking I/O.
     */
    fun durationMs(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val raw = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            raw?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                // MediaMetadataRetriever.release() can throw on some OEM builds;
                // safe to ignore since we're done with the retriever either way.
            }
        }
    }

    /**
     * Divides [songDurationMs] evenly across [clipUris] and trims each clip
     * to its slice, starting from 0 in each source clip. If a clip is
     * shorter than its slice, the clip's own full duration is used instead
     * (so we never ask the player/exporter to play past the end of a
     * clip). Slices are floored at 500ms to avoid degenerate zero-length
     * cuts when there are many clips and a short song.
     *
     * Must be called off the main thread since it reads clip durations via
     * [durationMs].
     */
    fun buildEqualCutPlan(
        context: Context,
        clipUris: List<Uri>,
        songDurationMs: Long
    ): List<ClipCut> {
        if (clipUris.isEmpty() || songDurationMs <= 0L) return emptyList()

        val sliceMs = songDurationMs / clipUris.size
        return clipUris.map { uri ->
            val clipDurationMs = durationMs(context, uri)
            val endMs = if (clipDurationMs in 1 until sliceMs) {
                clipDurationMs
            } else {
                sliceMs
            }
            ClipCut(uri = uri, startMs = 0L, endMs = endMs.coerceAtLeast(500L))
        }
    }

    /**
     * Total duration of a cut plan in milliseconds (sum of each clip's
     * trimmed length). Used to compare against the song length when
     * clipping the audio track for export.
     */
    fun totalDurationMs(plan: List<ClipCut>): Long =
        plan.sumOf { it.endMs - it.startMs }
}
