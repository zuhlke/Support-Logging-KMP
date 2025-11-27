package com.zuhlke.logging.viewer.baselineprofile

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log

class FakeLogContentProvider : ContentProvider() {

    data class FakeLog(
        val id: Int,
        val timestamp: Long,
        val severity: String,
        val message: String,
        val tag: String,
        val throwable: String?,
        val appRunId: Int
    )

    data class FakeAppRun(
        val id: Int,
        val launchDate: Long,
        val appVersion: String,
        val operatingSystemVersion: String,
        val device: String
    )

    val logs = mutableListOf<FakeLog>()
    val appRuns = mutableListOf<FakeAppRun>()

    companion object {
        private const val LOGS_PATH = "logs"
        private const val APP_RUNS_PATH = "appRuns"
    }

    override fun getType(uri: Uri): String? = when (uri.lastPathSegment) {
        LOGS_PATH -> "vnd.android.cursor.dir/vnd.com.zuhlke.logging.logs"
        APP_RUNS_PATH -> "vnd.android.cursor.dir/vnd.com.zuhlke.logging.appruns"
        else -> null
    }

    override fun onCreate(): Boolean {
        appRuns.add(FakeAppRun(1, 1625079600000, "1.0.0", "Android 15", "Pixel 6"))
        appRuns.add(FakeAppRun(2, 1625080020000, "1.0.0", "Android 15", "Pixel 6"))
        appRuns.add(FakeAppRun(3, 1625080380000, "1.0.1", "Android 15", "Pixel 6"))
        appRuns.add(FakeAppRun(4, 1625080800000, "1.0.1", "Android 15", "Pixel 6"))
        appRuns.add(FakeAppRun(5, 1625081160000, "1.0.2", "Android 15", "Pixel 6"))

        logs.add(FakeLog(1, 1625079600000, "Verbose", "App started", "MainActivity", null, 1))
        logs.add(FakeLog(2, 1625079660000, "Info", "User logged in", "AuthService", null, 1))
        logs.add(
            FakeLog(
                3,
                1625079720000,
                "Error",
                "Failed to load data",
                "DataService",
                "StackTrace here",
                1
            )
        )
        logs.add(
            FakeLog(
                4,
                1625079780000,
                "Debug",
                "Data loaded successfully",
                "DataService",
                null,
                1
            )
        )
        logs.add(FakeLog(5, 1625079840000, "Warn", "Low memory warning", "System", null, 1))
        logs.add(
            FakeLog(
                6,
                1625079900000,
                "Assert",
                "Critical failure",
                "CoreService",
                "StackTrace here",
                1
            )
        )
        logs.add(FakeLog(7, 1625079960000, "Info", "App closed", "MainActivity", null, 1))

        logs.add(FakeLog(8, 1625080020000, "Verbose", "App started", "MainActivity", null, 2))
        logs.add(FakeLog(9, 1625080080000, "Info", "User logged out", "AuthService", null, 2))
        logs.add(FakeLog(10, 1625080140000, "Debug", "Cache cleared", "CacheService", null, 2))
        logs.add(FakeLog(11, 1625080200000, "Warn", "Network slow", "NetworkService", null, 2))
        logs.add(
            FakeLog(
                12,
                1625080260000,
                "Error",
                "Crash detected",
                "CrashHandler",
                "StackTrace here",
                2
            )
        )
        logs.add(FakeLog(13, 1625080320000, "Info", "App restarted", "MainActivity", null, 2))

        logs.add(FakeLog(14, 1625080380000, "Verbose", "App started", "MainActivity", null, 3))
        logs.add(FakeLog(15, 1625080440000, "Info", "User signed up", "AuthService", null, 3))
        logs.add(FakeLog(16, 1625080500000, "Debug", "Feature toggled", "FeatureService", null, 3))
        logs.add(FakeLog(17, 1625080560000, "Warn", "Battery low", "System", null, 3))
        logs.add(
            FakeLog(
                18,
                1625080620000,
                "Error",
                "Database locked",
                "DatabaseService",
                "StackTrace here",
                3
            )
        )
        logs.add(
            FakeLog(
                19,
                1625080680000,
                "Assert",
                "Unexpected state",
                "StateService",
                "StackTrace here",
                3
            )
        )
        logs.add(FakeLog(20, 1625080740000, "Info", "App closed", "MainActivity", null, 3))

        logs.add(FakeLog(21, 1625080800000, "Verbose", "App started", "MainActivity", null, 4))
        logs.add(FakeLog(22, 1625080860000, "Info", "User logged in", "AuthService", null, 4))
        logs.add(
            FakeLog(
                23,
                1625080920000,
                "Debug",
                "Settings updated",
                "SettingsService",
                null,
                4
            )
        )
        logs.add(FakeLog(24, 1625080980000, "Warn", "Storage almost full", "System", null, 4))
        logs.add(
            FakeLog(
                25,
                1625081040000,
                "Error",
                "Failed to sync",
                "SyncService",
                "StackTrace here",
                4
            )
        )
        logs.add(FakeLog(26, 1625081100000, "Info", "App closed", "MainActivity", null, 4))

        logs.add(FakeLog(27, 1625081160000, "Verbose", "App started", "MainActivity", null, 5))
        logs.add(FakeLog(28, 1625081220000, "Info", "User registered", "AuthService", null, 5))
        logs.add(
            FakeLog(
                29,
                1625081280000,
                "Debug",
                "Notification sent",
                "NotificationService",
                null,
                5
            )
        )
        logs.add(FakeLog(30, 1625081340000, "Warn", "High CPU usage", "System", null, 5))
        logs.add(
            FakeLog(
                31,
                1625081400000,
                "Error",
                "Timeout occurred",
                "NetworkService",
                "StackTrace here",
                5
            )
        )
        logs.add(FakeLog(32, 1625081460000, "Info", "App closed", "MainActivity", null, 5))

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        val afterId = uri.getQueryParameter("afterId")?.toIntOrNull() ?: -1
        return when (uri.lastPathSegment) {
            LOGS_PATH -> {
                val logs = getLogsAfter(afterId)
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
                            log.timestamp,
                            log.severity,
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
                val appRuns = getAppRunsAfter(afterId)
                Log.d("LoggingContentProvider", "Found appRuns: ${appRuns.size}")
                val cursor = MatrixCursor(
                    arrayOf("id", "launchDate", "appVersion", "operatingSystemVersion", "device")
                )
                for (appRun in appRuns) {
                    cursor.addRow(
                        listOf(
                            appRun.id,
                            appRun.launchDate,
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

    private fun getLogsAfter(afterId: Int): List<FakeLog> = logs.filter { it.id > afterId }

    private fun getAppRunsAfter(afterId: Int): List<FakeAppRun> = appRuns.filter { it.id > afterId }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String?>?): Int = 0
}
