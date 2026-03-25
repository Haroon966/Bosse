package dev.olufsen.bosse.ui.detail

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.olufsen.bosse.R
import dev.olufsen.bosse.data.db.EpisodeEntity
import dev.olufsen.bosse.data.db.SeriesEntity
import dev.olufsen.bosse.player.PlayerActivity
import dev.olufsen.bosse.ui.LocalBosseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun SeriesDetailScreen(seriesId: Long, onBack: () -> Unit) {
    val app = LocalBosseApp.current
    val context = LocalContext.current
    var series by remember { mutableStateOf<SeriesEntity?>(null) }
    val episodes by app.libraryRepository.observeEpisodes(seriesId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(seriesId) {
        series = withContext(Dispatchers.IO) { app.libraryRepository.getSeries(seriesId) }
    }

    val s = series
    if (s == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Back")
        }
        AsyncImage(
            model = s.posterUrl,
            contentDescription = s.title,
            modifier = Modifier
                .width(160.dp)
                .padding(bottom = 12.dp),
            contentScale = ContentScale.Fit,
        )
        Text(text = s.title, modifier = Modifier.padding(bottom = 8.dp))
        s.overview?.let { Text(text = it, modifier = Modifier.padding(bottom = 16.dp)) }

        TvLazyColumn(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            pivotOffsets = PivotOffsets(parentFraction = 0.1f),
        ) {
            items(episodes, key = { it.id }) { ep ->
                EpisodeRow(
                    episode = ep,
                    onPlay = { seasonNum, resume ->
                        PlayerActivity.start(
                            activity = context as Activity,
                            uri = ep.fileUri,
                            title = "${s.title} — ${ep.title}",
                            playableType = "episode",
                            playableId = ep.id,
                            resumeMs = resume,
                            subtitleUri = ep.subtitleUri,
                            seriesId = seriesId,
                            seasonNum = seasonNum,
                            episodeNum = ep.episodeNumber,
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpisodeRow(
    episode: EpisodeEntity,
    onPlay: (seasonNum: Int, resumeMs: Long) -> Unit,
) {
    val app = LocalBosseApp.current
    var seasonNum by remember { mutableIntStateOf(1) }
    var resumeMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(episode.id) {
        val season = withContext(Dispatchers.IO) {
            app.database.seasonDao().getById(episode.seasonId)
        }
        seasonNum = season?.seasonNumber ?: 1
        val p = withContext(Dispatchers.IO) {
            app.libraryRepository.progressFor("episode", episode.id)
        }
        resumeMs = p?.let { pr ->
            val d = pr.durationMs
            if (d > 0 && pr.positionMs in 30_000L until d - 30_000L) pr.positionMs else 0L
        } ?: 0L
    }

    val label = if (resumeMs > 0) stringResource(R.string.resume) else stringResource(R.string.play)

    Button(
        onClick = { onPlay(seasonNum, resumeMs) },
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        Text("S${seasonNum}E${episode.episodeNumber} · ${episode.title} — $label")
    }
}
