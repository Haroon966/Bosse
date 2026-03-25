package dev.olufsen.bosse.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "series",
    indices = [
        Index(value = ["title"]),
    ],
)
data class SeriesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val posterUrl: String?,
    val overview: String?,
    val tmdbId: Int?,
    val addedAt: Long = System.currentTimeMillis(),
)
