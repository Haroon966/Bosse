package dev.olufsen.bosse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchProgressDao {
    @Query("SELECT * FROM watch_progress ORDER BY lastPlayedAt DESC LIMIT 24")
    fun observeRecent(): Flow<List<WatchProgressEntity>>

    @Query("SELECT * FROM watch_progress WHERE playableType = :type AND playableId = :id LIMIT 1")
    suspend fun get(type: String, id: Long): WatchProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatchProgressEntity): Long

    @Query("DELETE FROM watch_progress WHERE playableType = :type AND playableId = :id")
    suspend fun delete(type: String, id: Long)
}
