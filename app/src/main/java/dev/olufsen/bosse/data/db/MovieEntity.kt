package dev.olufsen.bosse.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movies",
    indices = [
        Index(value = ["fileUri"], unique = true),
        Index(value = ["addedAt"]),
        Index(value = ["title"]),
    ],
)
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileUri: String,
    val title: String,
    val year: Int?,
    val sizeBytes: Long,
    val lastModified: Long,
    val posterUrl: String?,
    val overview: String?,
    val tmdbId: Int?,
    val addedAt: Long = System.currentTimeMillis(),
)
