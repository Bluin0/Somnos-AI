package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E3E85),
    onPrimaryContainer = Color(0xFFE8DEF8),
    secondary = SoftPurpleOnContainer,
    secondaryContainer = Color(0xFF1E2E40),
    onSecondaryContainer = Color(0xFFD3E4FF),
    background = Color(0xFF12131A),
    surface = Color(0xFF1B1C22),
    onBackground = Color(0xFFE1E2EC),
    onSurface = Color(0xFFE1E2EC),
    outline = BorderC4,
    outlineVariant = BorderE1
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = SoftPurpleContainer,
    onPrimaryContainer = SoftPurpleOnContainer,
    secondary = SoftPurpleOnContainer,
    secondaryContainer = SoftBlueContainer,
    onSecondaryContainer = SoftBlueOnContainer,
    background = BgF8,
    surface = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = BorderC4,
    outlineVariant = BorderE1
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

