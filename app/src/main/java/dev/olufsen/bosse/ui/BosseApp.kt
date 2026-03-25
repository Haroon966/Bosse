package dev.olufsen.bosse.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.olufsen.bosse.ui.detail.MovieDetailScreen
import dev.olufsen.bosse.ui.detail.SeriesDetailScreen
import dev.olufsen.bosse.ui.home.HomeScreen
import dev.olufsen.bosse.ui.settings.SettingsScreen
import dev.olufsen.bosse.ui.theme.BosseTheme

@Composable
fun BosseApp() {
    BosseTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("settings") {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = "movie/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("movieId") ?: return@composable
                MovieDetailScreen(movieId = id, onBack = { navController.popBackStack() })
            }
            composable(
                route = "series/{seriesId}",
                arguments = listOf(navArgument("seriesId") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("seriesId") ?: return@composable
                SeriesDetailScreen(seriesId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}
