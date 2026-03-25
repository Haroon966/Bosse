package dev.olufsen.bosse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SeasonEntity?

    @Query("SELECT * FROM seasons WHERE seriesId = :seriesId ORDER BY seasonNumber")
    suspend fun seasonsForSeries(seriesId: Long): List<SeasonEntity>

    @Query("SELECT * FROM seasons WHERE seriesId = :seriesId AND seasonNumber = :num LIMIT 1")
    suspend fun get(seriesId: Long, num: Int): SeasonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(season: SeasonEntity): Long
}
