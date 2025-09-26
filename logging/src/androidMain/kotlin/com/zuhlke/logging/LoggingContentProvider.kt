package com.zuhlke.logging

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime

internal class LoggingContentProvider : ContentProvider() {

    companion object {
        private const val LOGS_PATH = "logs"
        private const val APP_RUNS_PATH = "appRuns"
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return when (uri.lastPathSegment) {
            LOGS_PATH -> "vnd.android.cursor.dir/vnd.com.zuhlke.logging.logs"
            APP_RUNS_PATH -> "vnd.android.cursor.dir/vnd.com.zuhlke.logging.appruns"
            else -> null
        }
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        return null
    }

    @OptIn(ExperimentalTime::class)
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        Log.d("LoggingContentProvider", "Querying from $uri")
        val afterId = uri.getQueryParameter("afterId")?.toIntOrNull() ?: -1
        return when (uri.lastPathSegment) {
            LOGS_PATH -> {
                val logs = runBlocking { SharedLogDaoHolder.logDao.getLogsAfter(afterId) }
                Log.d("LoggingContentProvider", "Found logs: ${logs.size}")
                val cursor = MatrixCursor(
                    arrayOf(
                        "id",
                        "timestamp",
                        "severity",
                        "message",
                        "tag",
                        "throwable",
                        "appRunId"
                    )
                )
                for (log in logs) {
                    cursor.addRow(
                        listOf(
                            log.id,
                            log.timestamp.toEpochMilliseconds(),
                            log.severity.name,
                            log.message,
                            log.tag,
                            log.throwable,
                            log.appRunId
                        )
                    )
                }
                cursor
            }

            APP_RUNS_PATH -> {
                val appRuns = runBlocking { SharedLogDaoHolder.logDao.getAppRunsAfter(afterId) }
                Log.d("LoggingContentProvider", "Found appRuns: ${appRuns.size}")
                val cursor = MatrixCursor(
                    arrayOf("id", "launchDate", "appVersion", "operatingSystemVersion", "device")
                )
                for (appRun in appRuns) {
                    cursor.addRow(
                        listOf(
                            appRun.id,
                            appRun.launchDate.toEpochMilliseconds(),
                            appRun.appVersion,
                            appRun.operatingSystemVersion,
                            appRun.device
                        )
                    )
                }
                cursor
            }

            else -> null
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int {
        return 0
    }
}