package dev.olufsen.bosse.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.olufsen.bosse.BosseApplication

class LibraryScanWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext.applicationContext as BosseApplication
        val enrich = inputData.getBoolean(KEY_ENRICH_TMDB, false)
        return try {
            app.libraryRepository.scanAllRoots(enrichTmdb = enrich)
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_ENRICH_TMDB = "enrich_tmdb"
        const val UNIQUE_NAME = "bosse_library_scan"
    }
}
