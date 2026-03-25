package dev.olufsen.bosse.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bosse_settings")

class SettingsRepository(private val context: Context) {
    private val tmdbKey = stringPreferencesKey("tmdb_api_key")

    val tmdbApiKey: Flow<String> = context.dataStore.data.map { it[tmdbKey].orEmpty() }

    suspend fun setTmdbApiKey(value: String) {
        context.dataStore.edit { it[tmdbKey] = value.trim() }
    }
}
