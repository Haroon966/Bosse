package dev.olufsen.bosse.data.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("year") year: Int? = null,
    ): TmdbMovieSearchResponse

    @GET("search/tv")
    suspend fun searchTv(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
    ): TmdbTvSearchResponse
}

@Serializable
data class TmdbMovieSearchResponse(
    val results: List<TmdbMovieResult> = emptyList(),
)

@Serializable
data class TmdbTvSearchResponse(
    val results: List<TmdbTvResult> = emptyList(),
)

@Serializable
data class TmdbMovieResult(
    val id: Int,
    val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    val overview: String? = null,
)

@Serializable
data class TmdbTvResult(
    val id: Int,
    val name: String,
    @SerialName("poster_path") val posterPath: String? = null,
    val overview: String? = null,
)

fun posterUrl(path: String?): String? =
    path?.let { "https://image.tmdb.org/t/p/w342$it" }
