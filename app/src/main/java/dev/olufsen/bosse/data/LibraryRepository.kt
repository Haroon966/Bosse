package dev.olufsen.bosse.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dev.olufsen.bosse.data.db.BosseDatabase
import dev.olufsen.bosse.data.db.EpisodeEntity
import dev.olufsen.bosse.data.db.LibraryRootEntity
import dev.olufsen.bosse.data.db.MovieEntity
import dev.olufsen.bosse.data.db.SeasonEntity
import dev.olufsen.bosse.data.db.SeriesEntity
import dev.olufsen.bosse.data.db.WatchProgressEntity
import dev.olufsen.bosse.data.scan.FilenameClassifier
import dev.olufsen.bosse.data.tmdb.TmdbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class LibraryRepository(
    private val context: Context,
    private val db: BosseDatabase,
    private val tmdbRepository: TmdbRepository,
) {
    private val isScanning = AtomicBoolean(false)
    private val scanningMutable = MutableStateFlow(false)
    val scanning: StateFlow<Boolean> = scanningMutable.asStateFlow()

    fun observeRoots(): Flow<List<LibraryRootEntity>> = db.libraryRootDao().observeRoots()

    fun observeMovies(): Flow<List<MovieEntity>> = db.movieDao().observeAll()

    fun observeSeries(): Flow<List<SeriesEntity>> = db.seriesDao().observeAll()

    fun observeEpisodes(seriesId: Long): Flow<List<EpisodeEntity>> =
        db.episodeDao().observeEpisodesForSeries(seriesId)

    fun observeContinueWatching(): Flow<List<ContinueItem>> =
        db.watchProgressDao().observeRecent().flatMapLatest { rows ->
            flow {
                val items = withContext(Dispatchers.IO) {
                    rows.mapNotNull { row -> resolveContinueRow(row) }
                }
                emit(items)
            }
        }.flowOn(Dispatchers.IO)

    private suspend fun resolveContinueRow(row: WatchProgressEntity): ContinueItem? =
        when (row.playableType) {
            "movie" -> {
                val m = db.movieDao().getById(row.playableId) ?: return null
                ContinueItem.MovieItem(m, row)
            }
            "episode" -> {
                val ep = db.episodeDao().getById(row.playableId) ?: return null
                val season = db.seasonDao().getById(ep.seasonId) ?: return null
                val series = db.seriesDao().getById(season.seriesId) ?: return null
                ContinueItem.EpisodeItem(series, ep, row)
            }
            else -> null
        }

    suspend fun addLibraryRoot(treeUri: Uri, displayName: String?) {
        val uriString = treeUri.toString()
        db.libraryRootDao().insert(
            LibraryRootEntity(
                treeUri = uriString,
                displayName = displayName,
                grantedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun scanAllRoots(enrichTmdb: Boolean) = withContext(Dispatchers.IO) {
        if (!isScanning.compareAndSet(false, true)) return@withContext
        scanningMutable.value = true
        try {
            val roots = db.libraryRootDao().getRoots()
            val allVideoUris = mutableListOf<String>()
            for (root in roots) {
                val doc = DocumentFile.fromTreeUri(context, Uri.parse(root.treeUri)) ?: continue
                walkAndIngest(doc, root.treeUri, emptyList(), allVideoUris)
            }
            if (allVideoUris.isNotEmpty()) {
                db.movieDao().deleteNotInUris(allVideoUris)
                db.episodeDao().deleteNotInUris(allVideoUris)
            }
            if (enrichTmdb) {
                tmdbRepository.enrichMoviesIfNeeded()
                tmdbRepository.enrichSeriesIfNeeded()
            }
        } finally {
            isScanning.set(false)
            scanningMutable.value = false
        }
    }

    private suspend fun walkAndIngest(
        node: DocumentFile,
        rootUriString: String,
        pathSegments: List<String>,
        allVideoUris: MutableList<String>,
    ) {
        val children = node.listFiles() ?: return
        for (child in children) {
            if (!child.isDirectory) {
                val name = child.name ?: continue
                val ext = name.substringAfterLast('.', "").lowercase()
                if (ext !in FilenameClassifier.VIDEO_EXTENSIONS) continue
                val uri = child.uri
                val uriStr = uri.toString()
                val size = child.length()
                val lastMod = child.lastModified()
                val subtitleUri = subtitleSiblingUri(child)
                allVideoUris.add(uriStr)
                ingestVideoFile(uriStr, name, pathSegments, size, lastMod, subtitleUri)
            } else {
                val seg = child.name ?: continue
                walkAndIngest(child, rootUriString, pathSegments + seg, allVideoUris)
            }
        }
    }

    private suspend fun ingestVideoFile(
        uriStr: String,
        fileName: String,
        pathSegments: List<String>,
        sizeBytes: Long,
        lastModified: Long,
        subtitleUri: String?,
    ) {
        val classified = FilenameClassifier.classify(fileName, pathSegments)

        if (classified.isEpisode) {
            val seriesTitle = classified.seriesTitle ?: "Unknown Series"
            val seasonNum = classified.seasonNumber ?: 1
            val epNum = classified.episodeNumber ?: 1

            val seriesRow = db.seriesDao().getByTitle(seriesTitle)
            val seriesId = seriesRow?.id
                ?: db.seriesDao().insert(
                    SeriesEntity(
                        title = seriesTitle,
                        posterUrl = null,
                        overview = null,
                        tmdbId = null,
                    ),
                )

            val seasonRow = db.seasonDao().get(seriesId, seasonNum)
            val seasonId = seasonRow?.id
                ?: db.seasonDao().insert(SeasonEntity(seriesId = seriesId, seasonNumber = seasonNum))

            val existingEp = db.episodeDao().getByFileUri(uriStr)
            if (existingEp != null &&
                existingEp.sizeBytes == sizeBytes &&
                existingEp.lastModified == lastModified
            ) {
                return
            }

            val entity = EpisodeEntity(
                seasonId = seasonId,
                episodeNumber = epNum,
                title = classified.displayTitle,
                fileUri = uriStr,
                subtitleUri = subtitleUri,
                sizeBytes = sizeBytes,
                lastModified = lastModified,
            )
            if (existingEp != null) {
                db.episodeDao().insert(entity.copy(id = existingEp.id))
            } else {
                db.episodeDao().insert(entity)
            }
        } else {
            val existing = db.movieDao().getByFileUri(uriStr)
            if (existing != null &&
                existing.sizeBytes == sizeBytes &&
                existing.lastModified == lastModified
            ) {
                return
            }
            val movie = MovieEntity(
                fileUri = uriStr,
                title = classified.displayTitle,
                year = classified.year,
                sizeBytes = sizeBytes,
                lastModified = lastModified,
                posterUrl = existing?.posterUrl,
                overview = existing?.overview,
                tmdbId = existing?.tmdbId,
            )
            if (existing != null) {
                db.movieDao().insert(movie.copy(id = existing.id))
            } else {
                db.movieDao().insert(movie)
            }
        }
    }

    private fun subtitleSiblingUri(videoFile: DocumentFile): String? {
        val parent = videoFile.parentFile ?: return null
        val base = videoFile.name?.substringBeforeLast('.', "") ?: return null
        val siblings = parent.listFiles() ?: return null
        for (f in siblings) {
            if (f.isFile && f.name.equals("$base.srt", ignoreCase = true)) {
                return f.uri.toString()
            }
        }
        return null
    }

    suspend fun upsertWatchProgress(
        type: String,
        id: Long,
        positionMs: Long,
        durationMs: Long,
    ) {
        db.watchProgressDao().insert(
            WatchProgressEntity(
                playableType = type,
                playableId = id,
                positionMs = positionMs,
                durationMs = durationMs,
                lastPlayedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun getMovie(id: Long): MovieEntity? = db.movieDao().getById(id)

    suspend fun getSeries(id: Long): SeriesEntity? = db.seriesDao().getById(id)

    suspend fun getEpisode(id: Long): EpisodeEntity? = db.episodeDao().getById(id)

    suspend fun nextEpisode(seriesId: Long, seasonNum: Int, epNum: Int): EpisodeEntity? =
        db.episodeDao().nextAfter(seriesId, seasonNum, epNum)

    suspend fun progressFor(type: String, id: Long): WatchProgressEntity? =
        db.watchProgressDao().get(type, id)
}
