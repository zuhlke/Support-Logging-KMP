package com.zuhlke.logging.viewer.data

import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class LogRepository @AssistedInject constructor(
    @Assisted val authority: String,
    private val fetchLogs: FetchLogs,
    private val fetchAppRuns: FetchAppRuns
) {
    private val allRuns = mutableListOf<AppRun>()
    private val allLogs = mutableListOf<LogEntry>()

    fun getLogs(): Flow<List<AppRunWithLogs>> = flow {
        var lastKnownAppRunId = 0
        var lastKnownLogId = 0

        while(true) {
            val newRuns = fetchAppRuns(authority, lastKnownAppRunId)
            val newLogs = fetchLogs(authority, lastKnownLogId)
            Log.d(
                "LogRepository",
                "Fetched ${newRuns.size} new runs and ${newLogs.size} new logs for authority $authority"
            )
            if (newLogs.isNotEmpty() || newRuns.isNotEmpty()) {
                allRuns.addAll(newRuns)
                allLogs.addAll(newLogs)
                lastKnownAppRunId = allRuns.lastOrNull()?.id ?: lastKnownAppRunId
                lastKnownLogId = allLogs.lastOrNull()?.id ?: lastKnownLogId

                val logsByAppRunId = allLogs.groupBy { it.appRunId }
                val data = allRuns.map { appRun ->
                    AppRunWithLogs(
                        appRun = appRun,
                        logEntries = logsByAppRunId[appRun.id] ?: emptyList()
                    )
                }
                emit(data)
            }

            delay(1000)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(authority: String): LogRepository
    }
}
