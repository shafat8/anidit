package com.anidit.audio

import com.anidit.core.BeatMap

/**
 * Contract for turning a music file into a [BeatMap]. Not yet implemented —
 * see ARCHITECTURE.md build order. This is the next real milestone after
 * the UI/import flow is confirmed working end-to-end.
 */
interface AudioAnalysisEngine {
    suspend fun analyze(audioUri: String, onProgress: (Float) -> Unit = {}): BeatMap
}
