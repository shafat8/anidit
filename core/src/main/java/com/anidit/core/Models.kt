package com.anidit.core

/**
 * Shared data contracts for AniDit. Time values are in microseconds unless
 * noted otherwise. See root ARCHITECTURE.md for the full pipeline design.
 */

data class MediaClip(
    val id: String,
    val uri: String,
    val durationUs: Long,
    val width: Int,
    val height: Int,
    val fps: Float
)

data class Beat(val timeUs: Long, val strength: Float, val isDownbeat: Boolean)

enum class SectionType { INTRO, VERSE, BUILDUP, DROP, CHORUS, BREAKDOWN, OUTRO }

data class AudioSection(
    val startUs: Long,
    val endUs: Long,
    val type: SectionType,
    val energy: Float
)

data class BeatMap(
    val bpm: Float,
    val beats: List<Beat>,
    val sections: List<AudioSection>,
    val energyCurve: List<Float>,
    val energySampleIntervalUs: Long
)

enum class SceneCategory { ACTION, EMOTIONAL, DIALOGUE, ESTABLISHING, UNKNOWN }

data class SceneSegment(
    val clipId: String,
    val startUs: Long,
    val endUs: Long,
    val motionIntensity: Float,
    val brightness: Float,
    val hasFace: Boolean,
    val category: SceneCategory
)

data class SceneMap(val clipId: String, val segments: List<SceneSegment>)

enum class EffectType {
    ZOOM, SHAKE, MOTION_BLUR, DIRECTIONAL_BLUR, FLASH_WHITE, FLASH_BLACK,
    RGB_SPLIT, GLOW, VIGNETTE, FILM_GRAIN, CHROMATIC_ABERRATION,
    LIGHT_LEAK, LENS_FLARE, EDGE_GLOW, SHARPEN, SCALE_PUNCH, ROTATION
}

enum class TransitionType {
    HARD_CUT, WHIP_PAN, BLUR_DISSOLVE, GLITCH, ZOOM_BLUR, BLACK_FLASH, WHITE_FLASH
}

data class ColorGradeParams(
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val temperature: Float = 0f,
    val shadowsTint: String = "#000000",
    val highlightsTint: String = "#FFFFFF"
)

data class StyleProfile(
    val name: String,
    val cutDensity: Float,
    val preferredSceneCategories: List<SceneCategory>,
    val effectWeights: Map<EffectType, Float>,
    val colorGrade: ColorGradeParams,
    val speedRampBias: Float,
    val transitionPalette: List<TransitionType>
)

data class AppliedEffect(
    val type: EffectType,
    val startUs: Long,
    val endUs: Long,
    val intensity: Float,
    val params: Map<String, Float> = emptyMap()
)

data class EditPlanClipInstance(
    val clipId: String,
    val sourceInUs: Long,
    val sourceOutUs: Long,
    val timelineStartUs: Long,
    val playbackSpeed: Float,
    val effects: List<AppliedEffect>,
    val transitionIn: TransitionType?,
    val transitionOut: TransitionType?
)

data class EditPlan(
    val musicUri: String,
    val totalDurationUs: Long,
    val clipInstances: List<EditPlanClipInstance>,
    val styleProfile: StyleProfile
)

object StylePresets {
    val VELOCITY = StyleProfile(
        name = "Velocity",
        cutDensity = 0.9f,
        preferredSceneCategories = listOf(SceneCategory.ACTION, SceneCategory.UNKNOWN),
        effectWeights = mapOf(
            EffectType.ZOOM to 0.8f, EffectType.SHAKE to 0.6f,
            EffectType.RGB_SPLIT to 0.4f, EffectType.FLASH_WHITE to 0.5f,
            EffectType.SCALE_PUNCH to 0.7f
        ),
        colorGrade = ColorGradeParams(contrast = 0.3f, saturation = 0.2f),
        speedRampBias = 0.6f,
        transitionPalette = listOf(TransitionType.WHIP_PAN, TransitionType.ZOOM_BLUR)
    )

    val CINEMATIC = StyleProfile(
        name = "Cinematic",
        cutDensity = 0.3f,
        preferredSceneCategories = listOf(SceneCategory.EMOTIONAL, SceneCategory.ESTABLISHING),
        effectWeights = mapOf(
            EffectType.VIGNETTE to 0.6f, EffectType.FILM_GRAIN to 0.3f,
            EffectType.LENS_FLARE to 0.2f, EffectType.GLOW to 0.4f
        ),
        colorGrade = ColorGradeParams(contrast = 0.2f, saturation = -0.1f, temperature = -0.15f),
        speedRampBias = -0.4f,
        transitionPalette = listOf(TransitionType.BLUR_DISSOLVE, TransitionType.HARD_CUT)
    )

    val DARK_EMOTIONAL = StyleProfile(
        name = "Dark Emotional",
        cutDensity = 0.25f,
        preferredSceneCategories = listOf(SceneCategory.EMOTIONAL),
        effectWeights = mapOf(
            EffectType.VIGNETTE to 0.7f, EffectType.FILM_GRAIN to 0.4f, EffectType.GLOW to 0.3f
        ),
        colorGrade = ColorGradeParams(
            contrast = 0.35f, saturation = -0.3f, temperature = -0.25f, shadowsTint = "#0A0A1A"
        ),
        speedRampBias = -0.6f,
        transitionPalette = listOf(TransitionType.BLUR_DISSOLVE, TransitionType.BLACK_FLASH)
    )

    val FIGHT = StyleProfile(
        name = "Fight",
        cutDensity = 0.85f,
        preferredSceneCategories = listOf(SceneCategory.ACTION),
        effectWeights = mapOf(
            EffectType.SHAKE to 0.8f, EffectType.FLASH_WHITE to 0.6f,
            EffectType.MOTION_BLUR to 0.6f, EffectType.SCALE_PUNCH to 0.7f,
            EffectType.CHROMATIC_ABERRATION to 0.3f
        ),
        colorGrade = ColorGradeParams(contrast = 0.4f, saturation = 0.15f),
        speedRampBias = 0.7f,
        transitionPalette = listOf(TransitionType.WHIP_PAN, TransitionType.WHITE_FLASH)
    )

    fun all(): List<StyleProfile> = listOf(VELOCITY, CINEMATIC, DARK_EMOTIONAL, FIGHT)
}
