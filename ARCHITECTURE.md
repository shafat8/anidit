# AniDit — Architecture

## Current state (be precise about this)

**Working and real:**
- Full multi-module Gradle project that builds a real installable APK.
- Navigation flow: Home → Import → Style → Timeline → Export.
- Import screen: real Android system pickers for video clips (multi-select)
  and a song (single-select), using `ActivityResultContracts`. Selections
  are held in `ProjectState` and persist across screen navigation within a
  session. Toasts confirm each pick so failures are visible immediately
  instead of silently doing nothing.
- Prompt screen: real text input + tappable preset chips backed by 4 real
  `StyleProfile` presets defined in `core`.
- Design system: dark theme, violet→magenta gradient CTA, numbered step
  header, waveform-motif dividers — applied consistently across all screens.

**Not yet real (explicitly marked with TODO/doc comments in code):**
- Beat detection (`audio-analysis` is an interface only)
- Scene/motion detection (`video-analysis` is an interface only)
- The actual cut-decision algorithm (`decision-engine` is an interface only)
- Any rendering, effects, or export logic — the Timeline and Export screens
  are UI shells with a placeholder preview box and a no-op Export button

This split is intentional: everything listed as "working" has been reasoned
through and is meant to function correctly on install. Everything listed as
"not yet real" is honestly labeled rather than faked with a button that
looks functional but does nothing silently.

## Pipeline design

```
[Media Import] → [Audio Analysis] → BeatMap ─┐
              → [Video Analysis] → SceneMap ─┼─► [Decision Engine] → EditPlan
                                              │
              [Style Interpreter] ◄── prompt ─┘
                                              ▼
                                    [Timeline] → [Effects] → [Renderer] → [Export]
```

See `core/src/main/java/com/anidit/core/Models.kt` for the full data
contracts (`BeatMap`, `SceneMap`, `StyleProfile`, `EditPlan`, etc). Every
module downstream of import is a pure function over these types, which is
what makes it possible to build and test each stage independently.

## Next milestone

`audio-analysis`'s `AudioAnalysisEngine`: decode audio via `MediaExtractor`,
run onset detection, estimate BPM, segment into sections. Testable in
isolation against 2-3 tracks with known BPM before touching video analysis
or the decision engine.
