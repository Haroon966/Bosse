package dev.olufsen.bosse.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LibraryRootEntity::class,
        MovieEntity::class,
        SeriesEntity::class,
        SeasonEntity::class,
        EpisodeEntity::class,
        WatchProgressEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class BosseDatabase : RoomDatabase() {
    abstract fun libraryRootDao(): LibraryRootDao
    abstract fun movieDao(): MovieDao
    abstract fun seriesDao(): SeriesDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun watchProgressDao(): WatchProgressDao

    companion object {
        fun create(context: Context): BosseDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                BosseDatabase::class.java,
                "bosse.db",
            ).build()
    }
}
