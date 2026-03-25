package dev.olufsen.bosse.util

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.olufsen.bosse.work.LibraryScanWorker

fun enqueueLibraryScan(context: Context, enrichTmdb: Boolean) {
    val data: Data = workDataOf(LibraryScanWorker.KEY_ENRICH_TMDB to enrichTmdb)
    val req = OneTimeWorkRequestBuilder<LibraryScanWorker>()
        .setInputData(data)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        LibraryScanWorker.UNIQUE_NAME,
        ExistingWorkPolicy.REPLACE,
        req,
    )
}
