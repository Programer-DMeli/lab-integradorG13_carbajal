package com.carbajal.rickandmorty.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PortalColors = darkColorScheme(
    primary = Color(0xFFB8ED76), onPrimary = Color(0xFF173800),
    primaryContainer = Color(0xFF29452A), onPrimaryContainer = Color(0xFFD4F7BB),
    secondaryContainer = Color(0xFF263E42), onSecondaryContainer = Color(0xFFD2F1F0),
    background = Color(0xFF0E1515), surface = Color(0xFF0E1515),
    surfaceVariant = Color(0xFF263232), onSurface = Color(0xFFE6EEEE),
    onSurfaceVariant = Color(0xFFB7C6C3), error = Color(0xFFFFB4AB),
)

@Composable
fun RickandmortyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PortalColors, typography = Typography, content = content)
}
