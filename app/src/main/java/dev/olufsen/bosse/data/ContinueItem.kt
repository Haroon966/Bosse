package dev.olufsen.bosse.data

import dev.olufsen.bosse.data.db.EpisodeEntity
import dev.olufsen.bosse.data.db.MovieEntity
import dev.olufsen.bosse.data.db.SeriesEntity
import dev.olufsen.bosse.data.db.WatchProgressEntity

sealed class ContinueItem {
    abstract val progress: WatchProgressEntity

    data class MovieItem(
        val movie: MovieEntity,
        override val progress: WatchProgressEntity,
    ) : ContinueItem()

    data class EpisodeItem(
        val series: SeriesEntity,
        val episode: EpisodeEntity,
        override val progress: WatchProgressEntity,
    ) : ContinueItem()
}
