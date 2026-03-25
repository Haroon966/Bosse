package dev.olufsen.bosse.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watch_progress",
    indices = [
        Index(value = ["lastPlayedAt"]),
        Index(value = ["playableType", "playableId"], unique = true),
    ],
)
data class WatchProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** "movie" or "episode" */
    val playableType: String,
    val playableId: Long,
    val positionMs: Long,
    val durationMs: Long,
    val lastPlayedAt: Long,
)
