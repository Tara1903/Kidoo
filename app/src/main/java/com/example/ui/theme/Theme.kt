package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KidooLightColorScheme = lightColorScheme(
  primary = KidooPrimary,
  onPrimary = KidooOnPrimary,
  primaryContainer = KidooPrimaryContainer,
  onPrimaryContainer = KidooOnPrimaryContainer,
  secondary = KidooSecondary,
  onSecondary = KidooOnSecondary,
  secondaryContainer = KidooSecondaryContainer,
  onSecondaryContainer = KidooOnSecondaryContainer,
  tertiary = KidooTertiary,
  onTertiary = KidooOnTertiary,
  tertiaryContainer = KidooTertiaryContainer,
  onTertiaryContainer = KidooOnTertiary,
  background = KidooBackground,
  onBackground = KidooOnBackground,
  surface = KidooSurface,
  onSurface = KidooOnSurface,
  surfaceVariant = KidooSurfaceVariant,
  onSurfaceVariant = KidooOnSurfaceVariant,
  surfaceContainerLowest = KidooSurfaceContainerLowest,
  surfaceContainerLow = KidooSurfaceContainerLow,
  surfaceContainer = KidooSurfaceContainer,
  surfaceContainerHigh = KidooSurfaceContainerHigh,
  surfaceContainerHighest = KidooSurfaceContainerHighest,
  outline = KidooOutline,
  outlineVariant = KidooOutlineVariant,
  error = KidooError,
  onError = KidooOnError,
  errorContainer = KidooErrorContainer
)

private val KidooDarkColorScheme = darkColorScheme(
  primary = KidooPrimaryFixedDim,
  onPrimary = KidooOnPrimaryFixed,
  primaryContainer = KidooPrimary,
  onPrimaryContainer = KidooOnPrimaryContainer,
  secondary = KidooSecondaryFixedDim,
  onSecondary = KidooOnSecondaryContainer,
  surface = Color(0xFF151B2B),
  onSurface = Color(0xFFEEF0FF),
  background = Color(0xFF0F1420),
  onBackground = Color(0xFFEEF0FF)
)

@Composable
fun KidooTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) KidooDarkColorScheme else KidooLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
