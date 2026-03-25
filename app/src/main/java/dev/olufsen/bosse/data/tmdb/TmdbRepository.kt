package dev.olufsen.bosse.data.tmdb

import dev.olufsen.bosse.data.db.BosseDatabase
import dev.olufsen.bosse.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class TmdbRepository(
    private val db: BosseDatabase,
    private val settings: SettingsRepository,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(
            OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build(),
        )
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: TmdbApi = retrofit.create()

    suspend fun enrichMoviesIfNeeded() {
        val key = settings.tmdbApiKey.first().trim()
        if (key.isEmpty()) return
        val movies = db.movieDao().recent(200)
        for (m in movies) {
            if (m.tmdbId != null && m.posterUrl != null) continue
            try {
                val res = api.searchMovie(key, m.title, m.year)
                val hit = res.results.firstOrNull() ?: continue
                db.movieDao().insert(
                    m.copy(
                        tmdbId = hit.id,
                        posterUrl = posterUrl(hit.posterPath),
                        overview = hit.overview?.takeIf { it.isNotBlank() },
                    ),
                )
            } catch (_: Exception) {
                // Offline / rate limit — skip
            }
        }
    }

    suspend fun enrichSeriesIfNeeded() {
        val key = settings.tmdbApiKey.first().trim()
        if (key.isEmpty()) return
        val all = db.seriesDao().getAll()
        for (s in all) {
            if (s.tmdbId != null && s.posterUrl != null) continue
            try {
                val res = api.searchTv(key, s.title)
                val hit = res.results.firstOrNull() ?: continue
                db.seriesDao().update(
                    s.copy(
                        tmdbId = hit.id,
                        posterUrl = posterUrl(hit.posterPath),
                        overview = hit.overview?.takeIf { it.isNotBlank() },
                    ),
                )
            } catch (_: Exception) {
            }
        }
    }
}
