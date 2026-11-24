package com.example.voice

import android.Manifest
import android.app.Application
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeBackend
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Manages a Gemini Live API voice session for Saphira.
 *
 * Uses Firebase AI Logic so the API key never ships in the APK.
 * Model: native-audio Live preview for lowest-latency bidirectional speech.
 */
@OptIn(PublicPreviewAPI::class)
class VoiceViewModel(application: Application) : AndroidViewModel(application) {

  private val tag = "SaphiraVoice"

  private val _uiState = MutableStateFlow(VoiceUiState())
  val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

  private var liveSession: LiveSession? = null

  private val systemInstruction: Content = content {
    text(
      """
      You are Saphira — an autonomous agentic AI assistant and personal operating system.
      You are calm, precise, proactive, and slightly futuristic in tone.
      You help with scheduling, deep reasoning, task orchestration, and everyday life.
      Keep spoken replies concise (1–3 sentences) unless the user asks for detail.
      Address the user naturally. Never mention that you are a language model.
      When appropriate, offer to take the next concrete action.
      """.trimIndent()
    )
  }

  /** Creates (or re-creates) the LiveModel. Does not open a session yet. */
  private fun createLiveModel() =
    Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
      modelName = "gemini-2.5-flash-native-audio-preview-12-2025",
      generationConfig =
        liveGenerationConfig {
          responseModality = ResponseModality.AUDIO
          // Fenrir is a clear, grounded voice that fits an agentic assistant.
          speechConfig = SpeechConfig(voice = Voice("FENRIR"))
        },
      systemInstruction = systemInstruction,
    )

  /**
   * Connects to the Live API and starts a bidirectional audio conversation.
   * Requires RECORD_AUDIO to already be granted.
   */
  @RequiresPermission(Manifest.permission.RECORD_AUDIO)
  fun startConversation() {
    if (_uiState.value.isConnected || _uiState.value.isConnecting) return

    viewModelScope.launch {
      _uiState.update {
        it.copy(
          isConnecting = true,
          errorMessage = null,
          statusText = "Connecting to Saphira…",
        )
      }
      try {
        val model = createLiveModel()
        val session = model.connect()
        liveSession = session

        // SDK handles mic capture + PCM streaming + speaker playback.
        // enableInterruptions = true lets the user barge in while Saphira is speaking.
        session.startAudioConversation(enableInterruptions = true)

        _uiState.update {
          it.copy(
            isConnecting = false,
            isConnected = true,
            isListening = true,
            statusText = "Listening — speak now",
          )
        }
        Log.i(tag, "Live session started")
      } catch (e: Exception) {
        Log.e(tag, "Failed to start Live session", e)
        liveSession = null
        _uiState.update {
          it.copy(
            isConnecting = false,
            isConnected = false,
            isListening = false,
            errorMessage = e.message ?: "Unable to start voice session",
            statusText = "Disconnected",
          )
        }
      }
    }
  }

  /** Ends the current audio conversation and closes the session. */
  fun stopConversation() {
    viewModelScope.launch {
      try {
        liveSession?.stopAudioConversation()
        liveSession?.close()
      } catch (e: Exception) {
        Log.w(tag, "Error while stopping session", e)
      } finally {
        liveSession = null
        _uiState.update {
          it.copy(
            isConnecting = false,
            isConnected = false,
            isListening = false,
            statusText = "Tap to talk with Saphira",
            errorMessage = null,
          )
        }
        Log.i(tag, "Live session stopped")
      }
    }
  }

  fun clearError() {
    _uiState.update { it.copy(errorMessage = null) }
  }

  override fun onCleared() {
    super.onCleared()
    try {
      liveSession?.stopAudioConversation()
      liveSession?.close()
    } catch (_: Exception) {
      // ignore
    }
    liveSession = null
  }
}

data class VoiceUiState(
  val isConnecting: Boolean = false,
  val isConnected: Boolean = false,
  val isListening: Boolean = false,
  val statusText: String = "Tap to talk with Saphira",
  val errorMessage: String? = null,
)
