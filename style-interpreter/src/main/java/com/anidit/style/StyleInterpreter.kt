package com.anidit.style

import com.anidit.core.StyleProfile

/**
 * Contract for turning a free-text prompt into a [StyleProfile]. Not yet
 * implemented — v1 will be rule-based keyword matching.
 */
interface StyleInterpreter {
    fun interpret(prompt: String?, basePreset: StyleProfile? = null): StyleProfile
}
