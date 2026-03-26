package dev.olufsen.bosse.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import dev.olufsen.bosse.R
import dev.olufsen.bosse.data.ContinueItem
import dev.olufsen.bosse.ui.LocalBosseApp
import dev.olufsen.bosse.ui.components.PosterCard
import dev.olufsen.bosse.ui.settings.OpenDocumentTreePersistable
import dev.olufsen.bosse.util.enqueueLibraryScan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val app = LocalBosseApp.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pickFolder = rememberLauncherForActivityResult(
        contract = OpenDocumentTreePersistable(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
        }
        scope.launch {
            val enrich = app.settingsRepository.tmdbApiKey.first().isNotBlank()
            app.libraryRepository.addLibraryRoot(uri, null)
            enqueueLibraryScan(context, enrichTmdb = enrich)
        }
    }

    val continueWatching by app.libraryRepository.observeContinueWatching()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val movies by app.libraryRepository.observeMovies()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val series by app.libraryRepository.observeSeries()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val roots by app.libraryRepository.observeRoots()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val scanning by app.libraryRepository.scanning.collectAsStateWithLifecycle()

    if (roots.isEmpty()) {
        CinemaEmptyState(
            scanning = scanning,
            onChooseLibrary = { pickFolder.launch(null) },
            onAddTmdbKey = { navController.navigate("settings") },
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        if (scanning) {
            Text(
                text = stringResource(R.string.updating_library),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }
        val recentMovies = remember(movies) { movies.take(20) }
        if (recentMovies.isNotEmpty()) {
            Text(
                text = stringResource(R.string.recently_added),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
            ) {
                items(recentMovies, key = { it.id }) { m ->
                    PosterCard(
                        title = m.title,
                        imageUrl = m.posterUrl,
                        onClick = { navController.navigate("movie/${m.id}") },
                    )
                }
            }
        }
        if (continueWatching.isNotEmpty()) {
            Text(
                text = stringResource(R.string.continue_watching),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
            ) {
                items(continueWatching) { item ->
                    ContinueCard(item, navController)
                }
            }
        }
        if (movies.isNotEmpty()) {
            Text(
                text = stringResource(R.string.movies),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
            ) {
                items(movies, key = { it.id }) { m ->
                    PosterCard(
                        title = m.title,
                        imageUrl = m.posterUrl,
                        onClick = { navController.navigate("movie/${m.id}") },
                    )
                }
            }
        }
        if (series.isNotEmpty()) {
            Text(
                text = stringResource(R.string.series),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
            ) {
                items(series, key = { it.id }) { s ->
                    PosterCard(
                        title = s.title,
                        imageUrl = s.posterUrl,
                        onClick = { navController.navigate("series/${s.id}") },
                    )
                }
            }
        }
        Button(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Text(stringResource(R.string.settings))
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ContinueCard(item: ContinueItem, navController: NavController) {
    when (item) {
        is ContinueItem.MovieItem -> PosterCard(
            title = item.movie.title,
            imageUrl = item.movie.posterUrl,
            onClick = { navController.navigate("movie/${item.movie.id}") },
        )
        is ContinueItem.EpisodeItem -> PosterCard(
            title = "${item.series.title} · ${item.episode.title}",
            imageUrl = item.series.posterUrl,
            onClick = { navController.navigate("series/${item.series.id}") },
        )
    }
}
