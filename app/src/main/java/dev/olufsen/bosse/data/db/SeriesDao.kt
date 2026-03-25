package dev.olufsen.bosse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series")
    suspend fun getAll(): List<SeriesEntity>

    @Query("SELECT * FROM series WHERE id = :id")
    suspend fun getById(id: Long): SeriesEntity?

    @Query("SELECT * FROM series WHERE title = :title COLLATE NOCASE LIMIT 1")
    suspend fun getByTitle(title: String): SeriesEntity?

    @Insert
    suspend fun insert(series: SeriesEntity): Long

    @Update
    suspend fun update(series: SeriesEntity)

    @Query("DELETE FROM series WHERE id = :id")
    suspend fun deleteById(id: Long)
}
