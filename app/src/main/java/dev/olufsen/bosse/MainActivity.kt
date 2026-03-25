package dev.olufsen.bosse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import dev.olufsen.bosse.ui.BosseApp
import dev.olufsen.bosse.ui.LocalBosseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as BosseApplication
        setContent {
            CompositionLocalProvider(LocalBosseApp provides app) {
                BosseApp()
            }
        }
    }
}
