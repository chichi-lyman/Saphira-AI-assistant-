package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.SaphiraConfig
import com.example.data.ConversationTurn
import com.example.data.SaphiraRepository
import com.example.data.SaphiraResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ChatRole {
  USER,
  SAPHIRA,
  NOTICE,
}

data class ChatMessage(val role: ChatRole, val text: String)

data class ChatUiState(
  val messages: List<ChatMessage> = emptyList(),
  val isSending: Boolean = false,
  val keyMissing: Boolean = false,
)

class ChatViewModel : ViewModel() {

  // The key comes from the .env file through the Secrets Gradle plugin
  // (see .env.example and the README). It is never written in code.
  private val apiKey = BuildConfig.GEMINI_API_KEY.trim()

  private val keyMissing = apiKey.isEmpty() || apiKey == SaphiraConfig.PLACEHOLDER_API_KEY

  private val repository = if (keyMissing) null else SaphiraRepository(apiKey)

  private val _uiState = MutableStateFlow(ChatUiState(keyMissing = keyMissing))
  val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

  fun send(text: String) {
    val repo = repository ?: return
    val trimmed = text.trim()
    if (trimmed.isEmpty() || _uiState.value.isSending) return

    _uiState.update {
      it.copy(messages = it.messages + ChatMessage(ChatRole.USER, trimmed), isSending = true)
    }

    // Send the whole conversation (minus on-screen notices) so Saphira
    // remembers what was already said in this chat.
    val history =
      _uiState.value.messages
        .filter { it.role != ChatRole.NOTICE }
        .map { ConversationTurn(fromUser = it.role == ChatRole.USER, text = it.text) }

    viewModelScope.launch {
      when (val result = repo.ask(history)) {
        is SaphiraResult.Answered ->
          _uiState.update {
            it.copy(
              messages = it.messages + ChatMessage(ChatRole.SAPHIRA, result.text),
              isSending = false,
            )
          }
        is SaphiraResult.Failed ->
          _uiState.update {
            it.copy(
              messages = it.messages + ChatMessage(ChatRole.NOTICE, result.message),
              isSending = false,
            )
          }
      }
    }
  }
}
