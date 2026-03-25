package dev.olufsen.bosse.player

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dev.olufsen.bosse.BosseApplication
import dev.olufsen.bosse.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : ComponentActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.playerView)

        val uri = intent.getStringExtra(EXTRA_URI) ?: run { finish(); return }
        val resumeMs = intent.getLongExtra(EXTRA_RESUME_MS, 0L)
        val subtitleUri = intent.getStringExtra(EXTRA_SUBTITLE_URI)

        val videoUri = Uri.parse(uri)
        val mediaItem = if (!subtitleUri.isNullOrBlank()) {
            MediaItem.Builder()
                .setUri(videoUri)
                .setSubtitleConfigurations(
                    listOf(
                        MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleUri))
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            .setLanguage("und")
                            .build(),
                    ),
                )
                .build()
        } else {
            MediaItem.fromUri(videoUri)
        }

        val exo = ExoPlayer.Builder(this).build().apply {
            setMediaItem(mediaItem)
            prepare()
            seekTo(resumeMs)
            playWhenReady = true
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            saveProgressIfNeeded(durationFallback = duration)
                        }
                    }
                },
            )
        }
        player = exo
        playerView.player = exo
    }

    override fun onStop() {
        super.onStop()
        player?.let { p ->
            saveProgressIfNeeded(durationFallback = p.duration)
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT || keyCode == KeyEvent.KEYCODE_CHANNEL_UP) {
            playNextEpisodeIfAny()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun playNextEpisodeIfAny() {
        val seriesId = intent.getLongExtra(EXTRA_SERIES_ID, -1L).takeIf { it >= 0 } ?: return
        val seasonNum = intent.getIntExtra(EXTRA_SEASON_NUM, -1).takeIf { it >= 0 } ?: return
        val epNum = intent.getIntExtra(EXTRA_EPISODE_NUM, -1).takeIf { it >= 0 } ?: return
        val app = application as BosseApplication
        lifecycleScope.launch {
            val next = withContext(Dispatchers.IO) {
                app.libraryRepository.nextEpisode(seriesId, seasonNum, epNum)
            } ?: return@launch
            val season = withContext(Dispatchers.IO) {
                app.database.seasonDao().getById(next.seasonId)
            } ?: return@launch
            player?.stop()
            player?.release()
            player = null
            PlayerActivity.start(
                this@PlayerActivity,
                uri = next.fileUri,
                title = next.title,
                playableType = "episode",
                playableId = next.id,
                resumeMs = 0L,
                subtitleUri = next.subtitleUri,
                seriesId = seriesId,
                seasonNum = season.seasonNumber,
                episodeNum = next.episodeNumber,
            )
            finish()
        }
    }

    private fun saveProgressIfNeeded(durationFallback: Long) {
        val p = player ?: return
        val type = intent.getStringExtra(EXTRA_PLAYABLE_TYPE) ?: return
        val id = intent.getLongExtra(EXTRA_PLAYABLE_ID, -1L).takeIf { it >= 0 } ?: return
        val pos = p.currentPosition
        val dur = when {
            p.duration > 0 -> p.duration
            durationFallback > 0 -> durationFallback
            else -> return
        }
        val app = application as BosseApplication
        lifecycleScope.launch(Dispatchers.IO) {
            app.libraryRepository.upsertWatchProgress(type, id, pos, dur)
        }
    }

    companion object {
        const val EXTRA_URI = "uri"
        const val EXTRA_TITLE = "title"
        const val EXTRA_PLAYABLE_TYPE = "playable_type"
        const val EXTRA_PLAYABLE_ID = "playable_id"
        const val EXTRA_RESUME_MS = "resume_ms"
        const val EXTRA_SUBTITLE_URI = "subtitle_uri"
        const val EXTRA_SERIES_ID = "series_id"
        const val EXTRA_SEASON_NUM = "season_num"
        const val EXTRA_EPISODE_NUM = "episode_num"

        fun start(
            activity: Activity,
            uri: String,
            title: String,
            playableType: String,
            playableId: Long,
            resumeMs: Long,
            subtitleUri: String?,
            seriesId: Long?,
            seasonNum: Int?,
            episodeNum: Int?,
        ) {
            val i = Intent(activity, PlayerActivity::class.java).apply {
                putExtra(EXTRA_URI, uri)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_PLAYABLE_TYPE, playableType)
                putExtra(EXTRA_PLAYABLE_ID, playableId)
                putExtra(EXTRA_RESUME_MS, resumeMs)
                putExtra(EXTRA_SUBTITLE_URI, subtitleUri)
                seriesId?.let { putExtra(EXTRA_SERIES_ID, it) }
                seasonNum?.let { putExtra(EXTRA_SEASON_NUM, it) }
                episodeNum?.let { putExtra(EXTRA_EPISODE_NUM, it) }
            }
            activity.startActivity(i)
        }
    }
}
