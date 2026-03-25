package dev.olufsen.bosse.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["seasonId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["fileUri"], unique = true),
        Index(value = ["seasonId", "episodeNumber"], unique = true),
        Index(value = ["seasonId"]),
    ],
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val seasonId: Long,
    val episodeNumber: Int,
    val title: String,
    val fileUri: String,
    val subtitleUri: String?,
    val sizeBytes: Long,
    val lastModified: Long,
    val addedAt: Long = System.currentTimeMillis(),
)
