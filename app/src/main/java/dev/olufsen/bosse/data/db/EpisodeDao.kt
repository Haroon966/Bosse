package dev.olufsen.bosse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE id = :id")
    suspend fun getById(id: Long): EpisodeEntity?

    @Query("SELECT * FROM episodes WHERE fileUri = :uri LIMIT 1")
    suspend fun getByFileUri(uri: String): EpisodeEntity?

    @Query(
        """
        SELECT e.* FROM episodes e
        INNER JOIN seasons s ON e.seasonId = s.id
        WHERE s.seriesId = :seriesId
        ORDER BY s.seasonNumber, e.episodeNumber
        """,
    )
    fun observeEpisodesForSeries(seriesId: Long): Flow<List<EpisodeEntity>>

    @Query(
        """
        SELECT e.* FROM episodes e
        INNER JOIN seasons s ON e.seasonId = s.id
        WHERE s.seriesId = :seriesId
        ORDER BY s.seasonNumber, e.episodeNumber
        """,
    )
    suspend fun episodesForSeries(seriesId: Long): List<EpisodeEntity>

    @Query(
        """
        SELECT e.* FROM episodes e
        INNER JOIN seasons s ON e.seasonId = s.id
        WHERE s.seriesId = :seriesId AND s.seasonNumber = :seasonNum AND e.episodeNumber = :epNum
        LIMIT 1
        """,
    )
    suspend fun getEpisode(seriesId: Long, seasonNum: Int, epNum: Int): EpisodeEntity?

    @Query(
        """
        SELECT e.* FROM episodes e
        INNER JOIN seasons s ON e.seasonId = s.id
        WHERE s.seriesId = :seriesId
        AND (s.seasonNumber > :seasonNum OR (s.seasonNumber = :seasonNum AND e.episodeNumber > :epNum))
        ORDER BY s.seasonNumber ASC, e.episodeNumber ASC
        LIMIT 1
        """,
    )
    suspend fun nextAfter(seriesId: Long, seasonNum: Int, epNum: Int): EpisodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(episode: EpisodeEntity): Long

    @Query("DELETE FROM episodes WHERE fileUri NOT IN (:keepUris)")
    suspend fun deleteNotInUris(keepUris: List<String>)
}
