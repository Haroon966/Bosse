package dev.olufsen.bosse.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.olufsen.bosse.BosseApplication

val LocalBosseApp = staticCompositionLocalOf<BosseApplication> {
    error("BosseApplication not provided")
}
