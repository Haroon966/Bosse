package dev.olufsen.bosse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getById(id: Long): MovieEntity?

    @Query("SELECT * FROM movies WHERE fileUri = :uri LIMIT 1")
    suspend fun getByFileUri(uri: String): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity): Long

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM movies WHERE fileUri NOT IN (:keepUris)")
    suspend fun deleteNotInUris(keepUris: List<String>)

    @Query("SELECT * FROM movies ORDER BY addedAt DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<MovieEntity>
}
