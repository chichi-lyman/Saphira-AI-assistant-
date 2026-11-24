package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SaphiraDarkColorScheme =
  darkColorScheme(
    primary = SaphiraPrimary,
    onPrimary = SaphiraOnPrimary,
    secondary = SaphiraAccent,
    onSecondary = SaphiraOnPrimary,
    tertiary = SaphiraPrimaryVariant,
    background = SaphiraDeep,
    onBackground = SaphiraOnSurface,
    surface = SaphiraSurface,
    onSurface = SaphiraOnSurface,
    error = SaphiraError,
  )

@Composable
fun SaphiraTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        dynamicDarkColorScheme(context)
      }
      else -> SaphiraDarkColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  SaphiraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
