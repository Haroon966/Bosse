package dev.olufsen.bosse.ui.detail

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.olufsen.bosse.R
import dev.olufsen.bosse.data.db.MovieEntity
import dev.olufsen.bosse.data.db.WatchProgressEntity
import dev.olufsen.bosse.player.PlayerActivity
import dev.olufsen.bosse.ui.LocalBosseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MovieDetailScreen(movieId: Long, onBack: () -> Unit) {
    val app = LocalBosseApp.current
    val context = LocalContext.current
    var movie by remember { mutableStateOf<MovieEntity?>(null) }
    var progress by remember { mutableStateOf<WatchProgressEntity?>(null) }
    var missing by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        val m = withContext(Dispatchers.IO) { app.libraryRepository.getMovie(movieId) }
        if (m == null) {
            missing = true
            return@LaunchedEffect
        }
        movie = m
        progress = withContext(Dispatchers.IO) { app.libraryRepository.progressFor("movie", m.id) }
    }

    if (missing) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val m = movie ?: return

    val resumeMs = progress?.let { p ->
        val d = p.durationMs
        if (d > 0 && p.positionMs in 30_000L until d - 30_000L) p.positionMs else 0L
    } ?: 0L
    val playLabel = if (resumeMs > 0) stringResource(R.string.resume) else stringResource(R.string.play)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Back")
        }
        AsyncImage(
            model = m.posterUrl,
            contentDescription = m.title,
            modifier = Modifier
                .width(200.dp)
                .height(300.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit,
        )
        Text(text = m.title, modifier = Modifier.padding(bottom = 8.dp))
        m.year?.let { Text(text = it.toString(), modifier = Modifier.padding(bottom = 8.dp)) }
        m.overview?.let { Text(text = it, modifier = Modifier.padding(bottom = 16.dp)) }
        Button(
            onClick = {
                PlayerActivity.start(
                    activity = context as Activity,
                    uri = m.fileUri,
                    title = m.title,
                    playableType = "movie",
                    playableId = m.id,
                    resumeMs = resumeMs,
                    subtitleUri = null,
                    seriesId = null,
                    seasonNum = null,
                    episodeNum = null,
                )
            },
        ) {
            Text(playLabel)
        }
    }
}
