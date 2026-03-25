package dev.olufsen.bosse.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.row.TvLazyRow
import androidx.tv.foundation.lazy.row.items
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import dev.olufsen.bosse.R
import dev.olufsen.bosse.data.ContinueItem
import dev.olufsen.bosse.ui.LocalBosseApp
import dev.olufsen.bosse.ui.components.PosterCard
@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val app = LocalBosseApp.current
    val continueWatching by app.libraryRepository.observeContinueWatching()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val movies by app.libraryRepository.observeMovies()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val series by app.libraryRepository.observeSeries()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val roots by app.libraryRepository.observeRoots()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val scanning by app.libraryRepository.scanning.collectAsStateWithLifecycle()

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
        if (roots.isEmpty()) {
            Text(
                text = stringResource(R.string.no_library_yet),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier.padding(horizontal = 32.dp),
            ) {
                Text(stringResource(R.string.pick_library_folder))
            }
        }
        val recentMovies = remember(movies) { movies.take(20) }
        if (recentMovies.isNotEmpty() && roots.isNotEmpty()) {
            Text(
                text = stringResource(R.string.recently_added),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            TvLazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
                pivotOffsets = PivotOffsets(parentFraction = 0.2f),
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
            TvLazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
                pivotOffsets = PivotOffsets(parentFraction = 0.2f),
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
            TvLazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
                pivotOffsets = PivotOffsets(parentFraction = 0.2f),
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
            TvLazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
                pivotOffsets = PivotOffsets(parentFraction = 0.2f),
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
