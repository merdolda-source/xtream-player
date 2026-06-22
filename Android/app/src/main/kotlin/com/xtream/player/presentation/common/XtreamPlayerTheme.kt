// Android/app/src/main/kotlin/com/xtream/player/presentation/common/XtreamPlayerTheme.kt
package com.xtream.player.presentation.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF5A5F),
    onPrimary = Color(0xFF2A0000),
    secondary = Color(0xFF35D6B0),
    background = Color(0xFF0B0B12),
    surface = Color(0xFF15151F),
    surfaceVariant = Color(0xFF24242F),
    onBackground = Color(0xFFF2F2F7),
    onSurface = Color(0xFFF2F2F7)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFE63946),
    secondary = Color(0xFF1B998B),
    background = Color(0xFFF7F7FA),
    surface = Color(0xFFFFFFFF)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun XtreamPlayerTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AppShapes,
        content = content
    )
}
