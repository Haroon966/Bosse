package dev.olufsen.bosse

import android.app.Application
import dev.olufsen.bosse.data.LibraryRepository
import dev.olufsen.bosse.data.db.BosseDatabase
import dev.olufsen.bosse.data.settings.SettingsRepository
import dev.olufsen.bosse.data.tmdb.TmdbRepository

class BosseApplication : Application() {

    lateinit var database: BosseDatabase
    lateinit var settingsRepository: SettingsRepository
    lateinit var tmdbRepository: TmdbRepository
    lateinit var libraryRepository: LibraryRepository

    override fun onCreate() {
        super.onCreate()
        database = BosseDatabase.create(this)
        settingsRepository = SettingsRepository(this)
        tmdbRepository = TmdbRepository(database, settingsRepository)
        libraryRepository = LibraryRepository(this, database, tmdbRepository)
    }
}
