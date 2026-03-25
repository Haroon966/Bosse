package dev.olufsen.bosse.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryRootDao {
    @Query("SELECT * FROM library_roots ORDER BY grantedAt DESC")
    fun observeRoots(): Flow<List<LibraryRootEntity>>

    @Query("SELECT * FROM library_roots ORDER BY grantedAt DESC")
    suspend fun getRoots(): List<LibraryRootEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(root: LibraryRootEntity)

    @Query("DELETE FROM library_roots WHERE treeUri = :uri")
    suspend fun deleteByUri(uri: String)
}
