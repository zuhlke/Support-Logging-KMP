package com.zuhlke.logging.viewer.data

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import com.zuhlke.logging.viewer.di.DISPATCHER_IO
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@OptIn(ExperimentalTime::class)
class FetchLogs @Inject constructor(
    @param:ApplicationContext val applicationContext: Context,
    @param:Named(DISPATCHER_IO) val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(authority: String, lastKnownId: Int): List<LogEntry> =
        withContext(dispatcher) {
            try {
                val uri = "content://$authority/logs?afterId=$lastKnownId".toUri()
                val cursor = applicationContext.contentResolver.query(
                    uri,
                    /* projection = */
                    null,
                    /* selection = */
                    null,
                    /* selectionArgs = */
                    null,
                    /* sortOrder = */
                    null
                )
                cursor?.use { cursor ->
                    buildList {
                        while (cursor.moveToNext()) {
                            val element = parseEntry(cursor, authority)
                            add(element)
                        }
                    }
                } ?: throw IllegalStateException("Cursor is null!")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error querying logs from authority $authority", e)
                throw e
            }
        }

    private fun parseEntry(cursor: Cursor, authority: String): LogEntry {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val timestamp =
            Instant.fromEpochMilliseconds(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")))
        val logMessage = cursor.getString(cursor.getColumnIndexOrThrow("message"))
        val severityAsString = cursor.getString(cursor.getColumnIndexOrThrow("severity"))
        val severity = Severity.valueOf(severityAsString)
        val tag = cursor.getString(cursor.getColumnIndexOrThrow("tag"))
        val throwable = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("throwable"))
        val appRunId = cursor.getInt(cursor.getColumnIndexOrThrow("appRunId"))
        Log.d("MainViewModel", "Log from $authority: $timestamp $logMessage")
        return LogEntry(
            id = id,
            timestamp = timestamp,
            severity = severity,
            message = logMessage,
            tag = tag,
            throwable = throwable,
            appRunId = appRunId
        )
    }
}
