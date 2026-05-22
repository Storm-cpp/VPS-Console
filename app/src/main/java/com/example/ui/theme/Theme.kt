package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = GoogleBlue,
  secondary = GoogleGreen,
  tertiary = GoogleYellow,
  background = GoogleDarkBg,
  surface = GoogleDarkSurface,
  surfaceVariant = GoogleDarkCard,
  onPrimary = Color(0xFF002251),
  onSecondary = Color(0xFF003816),
  onBackground = TextPrimary,
  onSurface = TextPrimary,
  onSurfaceVariant = TextPrimary,
  outline = BorderColor
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default as explicitly requested by user
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
