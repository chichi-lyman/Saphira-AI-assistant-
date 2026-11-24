package com.example.ui

import android.Manifest
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.voice.VoiceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceScreen(
  viewModel: VoiceViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

  LaunchedEffect(uiState.errorMessage) {
    uiState.errorMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearError()
    }
  }

  Scaffold(
    snackbarHost = {
      SnackbarHost(snackbarHostState) { data ->
        Snackbar(
          snackbarData = data,
          containerColor = Color(0xFF1A1A2E),
          contentColor = Color.White,
        )
      }
    },
    containerColor = Color.Transparent,
  ) { padding ->
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors =
                listOf(
                  Color(0xFF0B0B14),
                  Color(0xFF12122A),
                  Color(0xFF1A1035),
                )
            )
          )
          .padding(padding)
    ) {
      Image(
        painter = painterResource(R.drawable.img_saphira_orb),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier =
          Modifier
            .fillMaxSize()
            .align(Alignment.Center),
        alpha = 0.22f,
      )

      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "SAPHIRA",
            style =
              MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Color(0xFFE8E6FF),
              ),
          )
          Spacer(Modifier.height(6.dp))
          Text(
            text = "Voice · Live · Agentic",
            style =
              MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF9B8CFF),
                letterSpacing = 1.5.sp,
              ),
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          PulsingOrb(
            isActive = uiState.isConnected || uiState.isConnecting,
            isListening = uiState.isListening,
          )
          Spacer(Modifier.height(28.dp))
          Text(
            text = uiState.statusText,
            style =
              MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFFD0CCFF),
                textAlign = TextAlign.Center,
              ),
            textAlign = TextAlign.Center,
          )
          if (uiState.isConnecting) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(
              color = Color(0xFF8B7CFF),
              strokeWidth = 2.dp,
              modifier = Modifier.size(28.dp),
            )
          }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          when {
            !micPermission.status.isGranted -> {
              PermissionBlock(
                shouldShowRationale = micPermission.status.shouldShowRationale,
                onRequest = { micPermission.launchPermissionRequest() },
              )
            }
            uiState.isConnected -> {
              FloatingActionButton(
                onClick = { viewModel.stopConversation() },
                containerColor = Color(0xFFE74C6B),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(76.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.Stop,
                  contentDescription = "End conversation",
                  modifier = Modifier.size(32.dp),
                )
              }
              Spacer(Modifier.height(12.dp))
              Text(
                text = "End session",
                style =
                  MaterialTheme.typography.labelLarge.copy(color = Color(0xFFB0A8D0)),
              )
            }
            else -> {
              FloatingActionButton(
                onClick = {
                  if (micPermission.status.isGranted) {
                    viewModel.startConversation()
                  } else {
                    micPermission.launchPermissionRequest()
                  }
                },
                containerColor = Color(0xFF6C5CE7),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                modifier = Modifier.size(84.dp),
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Start talking",
                  modifier = Modifier.size(36.dp),
                )
              }
              Spacer(Modifier.height(12.dp))
              Text(
                text = "Hold the conversation open · interrupt anytime",
                style =
                  MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF8A82B0),
                    textAlign = TextAlign.Center,
                  ),
                textAlign = TextAlign.Center,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PulsingOrb(isActive: Boolean, isListening: Boolean) {
  val infinite = rememberInfiniteTransition(label = "orb")
  val scale by
    infinite.animateFloat(
      initialValue = 1f,
      targetValue = if (isActive) 1.08f else 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "scale",
    )
  val glowAlpha by
    infinite.animateFloat(
      initialValue = 0.35f,
      targetValue = if (isListening) 0.75f else 0.45f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(900, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "glow",
    )

  Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
    Box(
      modifier =
        Modifier
          .size(200.dp)
          .scale(scale)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors =
                listOf(
                  Color(0xFF6C5CE7).copy(alpha = glowAlpha * 0.55f),
                  Color.Transparent,
                )
            )
          )
    )
    Image(
      painter = painterResource(R.drawable.ic_saphira_foreground),
      contentDescription = "Saphira",
      contentScale = ContentScale.Crop,
      modifier =
        Modifier
          .size(148.dp)
          .clip(CircleShape)
          .border(
            width = 2.dp,
            brush =
              Brush.linearGradient(
                listOf(Color(0xFFA78BFA), Color(0xFF6C5CE7), Color(0xFF4C1D95))
              ),
            shape = CircleShape,
          ),
    )
  }
}

@Composable
private fun PermissionBlock(shouldShowRationale: Boolean, onRequest: () -> Unit) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(
      imageVector = Icons.Default.MicOff,
      contentDescription = null,
      tint = Color(0xFFB0A8D0),
      modifier = Modifier.size(40.dp),
    )
    Spacer(Modifier.height(12.dp))
    Text(
      text =
        if (shouldShowRationale) {
          "Microphone access is required for live voice with Saphira."
        } else {
          "Allow microphone access to begin a live conversation."
        },
      style =
        MaterialTheme.typography.bodyMedium.copy(
          color = Color(0xFFC8C0E8),
          textAlign = TextAlign.Center,
        ),
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(16.dp))
    Button(
      onClick = onRequest,
      colors =
        ButtonDefaults.buttonColors(
          containerColor = Color(0xFF6C5CE7),
          contentColor = Color.White,
        ),
    ) {
      Text("Grant microphone")
    }
  }
}
