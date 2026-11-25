# Saphira AI 🌹

**Saphira is the personal AI assistant of Chelsea Megan Woods (Chichi).** She lives in
Chelsea's pocket: one assistant, one personality — warm, direct, plain everyday words —
who knows her person and helps with her real life and her work: her music, her projects,
her schedule, her money questions, and whatever she is building next.

This repository is the native Android home of Saphira, written in Kotlin with
Jetpack Compose.

## What's built so far

**Piece 1 — the chat screen and the Gemini brain.**

- A real chat screen: your messages on the right in Saphira pink (#E84393), Saphira's
  replies on the left on a light blush background, a "Saphira is thinking…" state while
  she answers, and an honest notice in the chat if a message doesn't go through.
- Saphira talks to the Gemini API (`generateContent`) through Retrofit / OkHttp / Moshi,
  and the whole conversation is sent with each message so she remembers what was said.
- Her personality lives in **one string** in
  `app/src/main/java/com/example/SaphiraConfig.kt` (`SYSTEM_INSTRUCTION`). Edit that one
  string to change how she talks. The Gemini model name lives in the same file
  (`GEMINI_MODEL`) — one constant, one place to change it.
- **No fake replies.** If there is no API key, or Gemini can't be reached, the app says
  so in plain words instead of pretending.

## Add the Gemini key (required — this is the step that wakes her up)

Saphira's brain is Gemini, and she needs an API key. The key is **never** written in the
code and **never** committed to this repo — it goes in a local `.env` file that
git ignores.

1. Get a free key from **Google AI Studio**: go to
   [aistudio.google.com](https://aistudio.google.com), sign in, and choose
   **"Get API key"** → create a key.
2. In this project, copy `.env.example` and name the copy **`.env`** (same folder,
   right next to `.env.example`).
3. In `.env`, replace the placeholder so the line reads:
   `GEMINI_API_KEY=<your real key>`
4. Run the app again. Without this step the app still opens, but Saphira shows
   "Saphira needs her key to wake up" instead of answering.

## Open and run the project

1. Install **Android Studio** (latest version) if you don't have it.
2. Open Android Studio → **Open** → choose this project folder
   (`Saphira-AI-assistant-`).
3. Let Gradle sync finish (the first sync downloads Gradle and the libraries —
   it needs internet and can take a few minutes).
4. Do the `.env` step above so Saphira has her key.
5. Pick an emulator or plug in an Android phone (Android 7.0 / API 24 or newer) and
   press **Run ▶**.

## Project notes

- Package: `com.example` · App ID: `com.aistudio.saphira.v9k2m` · Min SDK 24 ·
  Target SDK 36.
- The scaffold also declares Firebase, Room, and other libraries for future pieces;
  piece 1 doesn't use them yet — the chat talks to the Gemini REST API directly with
  the key above. There is no `google-services.json` in this repo.
- `.env` is listed in `.gitignore`. Never commit a real key.
