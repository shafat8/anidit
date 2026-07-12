package com.anidit.video

import com.anidit.core.MediaClip
import com.anidit.core.SceneMap

/**
 * Contract for turning a [MediaClip] into a [SceneMap]. Not yet implemented.
 */
interface VideoAnalysisEngine {
    suspend fun analyze(clip: MediaClip, onProgress: (Float) -> Unit = {}): SceneMap
}
