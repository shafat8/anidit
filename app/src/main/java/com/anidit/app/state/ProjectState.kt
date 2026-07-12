package com.anidit.app.state

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

/**
 * In-memory state for the current editing session. Pragmatic singleton for
 * this build stage; will move to a ViewModel + persisted storage once
 * project save/resume is built.
 */
object ProjectState {
    val selectedClips = mutableStateListOf<Uri>()
    val selectedSong = mutableStateOf<Uri?>(null)
    val prompt = mutableStateOf("")
    val selectedPresetName = mutableStateOf<String?>(null)

    fun reset() {
        selectedClips.clear()
        selectedSong.value = null
        prompt.value = ""
        selectedPresetName.value = null
    }
}
