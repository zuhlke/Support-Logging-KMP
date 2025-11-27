package com.zuhlke.logging.viewer.data

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.zuhlke.logging.viewer.di.DISPATCHER_IO
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FetchAppRuns @Inject constructor(
    @param:ApplicationContext val context: Context,
    @param:Named(DISPATCHER_IO) val dispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(authority: String, lastKnownId: Int): List<AppRun> =
        withContext(dispatcher) {
            try {
                val uri = "content://$authority/appRuns?afterId=$lastKnownId".toUri()
                val cursor = context.contentResolver.query(
                    uri,
                    /* projection = */ null,
                    /* selection = */ null,
                    /* selectionArgs = */ null,
                    /* sortOrder = */ null
                )
                cursor?.use { cursor ->
                    buildList {
                        while (cursor.moveToNext()) {
                            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                            val launchDate =
                                Instant.fromEpochMilliseconds(
                                    cursor.getLong(
                                        cursor.getColumnIndexOrThrow("launchDate")
                                    )
                                )
                            val appVersion =
                                cursor.getString(cursor.getColumnIndexOrThrow("appVersion"))
                            val osVersion =
                                cursor.getString(
                                    cursor.getColumnIndexOrThrow("operatingSystemVersion")
                                )
                            val device = cursor.getString(cursor.getColumnIndexOrThrow("device"))
                            Log.d("MainViewModel", "AppRun from $authority: $id $launchDate")
                            add(AppRun(id, launchDate, appVersion, osVersion, device))
                        }
                    }
                } ?: emptyList<AppRun>().also {
                    Log.e("MainViewModel", "Cursor is null when querying authority $authority")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error querying logs from authority $authority", e)
                throw e
            }
        }
}
