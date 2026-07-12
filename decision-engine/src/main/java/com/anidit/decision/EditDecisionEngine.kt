package com.anidit.decision

import com.anidit.core.BeatMap
import com.anidit.core.EditPlan
import com.anidit.core.MediaClip
import com.anidit.core.SceneMap
import com.anidit.core.StyleProfile

/**
 * The core auto-edit logic: pure function from analysis + style to an
 * [EditPlan]. Not yet implemented — see ARCHITECTURE.md for the planned
 * algorithm.
 */
interface EditDecisionEngine {
    fun buildEditPlan(
        clips: List<MediaClip>,
        beatMap: BeatMap,
        sceneMaps: List<SceneMap>,
        styleProfile: StyleProfile
    ): EditPlan
}
