package dev.olufsen.bosse.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val BosseRed = Color(0xFFE50914)
private val BosseBg = Color(0xFF0D1117)
private val BosseSurface = Color(0xFF161B22)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BosseTheme(content: @Composable () -> Unit) {
    val dark = darkColorScheme(
        primary = BosseRed,
        secondary = BosseRed,
        background = BosseBg,
        surface = BosseSurface,
    )
    MaterialTheme(colorScheme = dark, content = content)
}
