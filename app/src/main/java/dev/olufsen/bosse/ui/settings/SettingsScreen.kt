package dev.olufsen.bosse.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import dev.olufsen.bosse.R
import dev.olufsen.bosse.ui.LocalBosseApp
import dev.olufsen.bosse.util.enqueueLibraryScan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val app = LocalBosseApp.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tmdbKey by app.settingsRepository.tmdbApiKey.collectAsStateWithLifecycle(initialValue = "")
    var keyDraft by remember(tmdbKey) { mutableStateOf(tmdbKey) }

    val pickFolder = rememberLauncherForActivityResult(
        contract = OpenDocumentTreePersistable(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
            // Some OEMs require a persistable grant from the picker intent; library may still work until reboot.
        }
        scope.launch {
            val enrich = app.settingsRepository.tmdbApiKey.first().isNotBlank()
            app.libraryRepository.addLibraryRoot(uri, null)
            enqueueLibraryScan(context, enrichTmdb = enrich)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Back")
        }
        Button(
            onClick = { pickFolder.launch(null) },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text(stringResource(R.string.pick_library_folder))
        }
        Button(
            onClick = {
                scope.launch {
                    enqueueLibraryScan(
                        context,
                        enrichTmdb = tmdbKey.isNotBlank(),
                    )
                }
            },
            modifier = Modifier.padding(bottom = 24.dp),
        ) {
            Text(stringResource(R.string.refresh_library))
        }
        Text(
            text = stringResource(R.string.tmdb_api_key),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value = keyDraft,
            onValueChange = { keyDraft = it },
            modifier = Modifier.padding(bottom = 16.dp),
            singleLine = true,
        )
        Button(
            onClick = {
                scope.launch {
                    app.settingsRepository.setTmdbApiKey(keyDraft)
                }
            },
        ) {
            Text(stringResource(R.string.save))
        }
    }
}
