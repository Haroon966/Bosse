package dev.olufsen.bosse.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_roots")
data class LibraryRootEntity(
    @PrimaryKey val treeUri: String,
    val displayName: String?,
    val grantedAt: Long,
)
