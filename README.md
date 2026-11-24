# Saphira AI — Gemini Live Voice

Autonomous agentic AI assistant with **real-time bidirectional voice** powered by the Gemini Live API via Firebase AI Logic.

## What's implemented

- **Gemini Live API** session (`gemini-2.5-flash-native-audio-preview-12-2025`)
- Bidirectional audio (mic → model → speaker) with barge-in support
- Saphira system persona (calm, proactive, concise)
- Dark cosmic UI using brand orb / foreground assets
- Runtime mic permission flow (Accompanist)
- App Check debug provider for local development
- No API key in the APK (Firebase AI Logic + secrets)

## Run locally

**Prerequisites:** Android Studio

1. Open the project and sync Gradle.
2. Create `.env` in the project root (see `.env.example`).
3. Register an **App Check debug token**:
   - Run once on emulator/device.
   - Copy token from Logcat (`DebugAppCheckProvider: Enter this debug secret…`).
   - Firebase Console → App Check → Manage debug tokens → add it.
4. Grant microphone permission when prompted.
5. Tap the mic → speak → Saphira answers live. Red stop ends the session.

## Key files

| Path | Role |
|------|------|
| `voice/VoiceViewModel.kt` | LiveModel + session lifecycle |
| `ui/VoiceScreen.kt` | Permission + orb UI + controls |
| `SaphiraApp.kt` | Firebase + App Check debug init |
| `ui/theme/*` | Saphira dark palette + custom fonts |

## Next upgrades

- Function calling (calendar, tasks, Workspace)
- Transcript captions alongside audio
- Proactive audio / wake phrases
- Voice picker (`FENRIR`, `PUCK`, `CHARON`, …)
