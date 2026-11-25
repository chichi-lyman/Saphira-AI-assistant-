package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// Saphira's colors: her settled pink on a light blush background.
private val SaphiraPink = Color(0xFFE84393)
private val SaphiraBlush = Color(0xFFFDF0F6)
private val SaphiraBubble = Color(0xFFFFFFFF)
private val NoticeBubble = Color(0xFFEFF0F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
  val state by chatViewModel.uiState.collectAsStateWithLifecycle()
  var input by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Keep the newest message (and the "thinking" bubble) in view.
  LaunchedEffect(state.messages.size, state.isSending) {
    val itemCount = state.messages.size + if (state.isSending) 1 else 0
    if (itemCount > 0) listState.animateScrollToItem(itemCount - 1)
  }

  Scaffold(
    containerColor = SaphiraBlush,
    topBar = {
      TopAppBar(
        title = { Text("Saphira 🌹", fontWeight = FontWeight.Bold) },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = SaphiraPink,
            titleContentColor = Color.White,
          ),
      )
    },
    bottomBar = {
      if (!state.keyMissing) {
        ChatInputBar(
          value = input,
          enabled = !state.isSending,
          onValueChange = { input = it },
          onSend = {
            chatViewModel.send(input)
            input = ""
          },
        )
      }
    },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      when {
        state.keyMissing -> KeyMissingState()
        state.messages.isEmpty() && !state.isSending -> EmptyState()
        else ->
          LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            items(state.messages) { message -> MessageBubble(message) }
            if (state.isSending) {
              item { TypingBubble() }
            }
          }
      }
    }
  }
}

@Composable
private fun EmptyState() {
  Column(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "Hey Chichi, I'm Saphira 🌹",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = SaphiraPink,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text =
        "I'm right here. Ask me anything — your day, your music, your next move — " +
          "and we'll sort it out together.",
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun KeyMissingState() {
  Column(
    modifier = Modifier.fillMaxSize().padding(32.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "Saphira needs her key to wake up 🌹",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = SaphiraPink,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text =
        "There's no Gemini API key in this build yet, so Saphira can't think or answer. " +
          "To wake her up: copy .env.example to .env in the project, put a real " +
          "GEMINI_API_KEY in it (a free key from Google AI Studio), then run the app " +
          "again. The steps are in the README.",
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
  when (message.role) {
    ChatRole.USER ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Surface(
          color = SaphiraPink,
          shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
          modifier = Modifier.widthIn(max = 300.dp),
        ) {
          Text(
            text = message.text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          )
        }
      }
    ChatRole.SAPHIRA ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Column(modifier = Modifier.widthIn(max = 300.dp)) {
          Text(
            text = "Saphira",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = SaphiraPink,
            modifier = Modifier.padding(start = 6.dp, bottom = 2.dp),
          )
          Surface(
            color = SaphiraBubble,
            shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
            shadowElevation = 1.dp,
          ) {
            Text(
              text = message.text,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            )
          }
        }
      }
    ChatRole.NOTICE ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
      ) {
        Surface(
          color = NoticeBubble,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.widthIn(max = 320.dp),
        ) {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          )
        }
      }
  }
}

@Composable
private fun TypingBubble() {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
    Surface(
      color = SaphiraBubble,
      shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
      shadowElevation = 1.dp,
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        CircularProgressIndicator(
          color = SaphiraPink,
          modifier = Modifier.size(16.dp),
          strokeWidth = 2.dp,
        )
        Text(text = "Saphira is thinking…", style = MaterialTheme.typography.bodyLarge)
      }
    }
  }
}

@Composable
private fun ChatInputBar(
  value: String,
  enabled: Boolean,
  onValueChange: (String) -> Unit,
  onSend: () -> Unit,
) {
  Surface(color = SaphiraBlush) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        placeholder = { Text("Talk to Saphira…") },
        maxLines = 4,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = { onSend() }),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.weight(1f),
      )
      FilledIconButton(
        onClick = onSend,
        enabled = enabled && value.isNotBlank(),
        colors =
          IconButtonDefaults.filledIconButtonColors(
            containerColor = SaphiraPink,
            contentColor = Color.White,
          ),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
        )
      }
    }
  }
}

