package com.zuhlke.logging.viewer.data

import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LogRepository @AssistedInject constructor(
    @Assisted val authority: String,
    private val fetchLogs: FetchLogs,
    private val fetchAppRuns: FetchAppRuns
) {
    private val allRuns = mutableListOf<AppRun>()
    private val allLogs = mutableListOf<LogEntry>()

    private val _data = MutableStateFlow<List<AppRunWithLogs>>(emptyList())
    val data: StateFlow<List<AppRunWithLogs>> = _data

    suspend fun fetch(
        lastKnownAppRunId: Int,
        lastKnownLogId: Int
    ): Pair<Int, Int> {
        val newRuns = fetchAppRuns(authority, lastKnownAppRunId)
        val newLogs = fetchLogs(authority, lastKnownLogId)
        Log.d(
            "LogRepository",
            "Fetched ${newRuns.size} new runs and ${newLogs.size} new logs for authority $authority"
        )
        newRuns.forEach { allRuns.add(it) }
        newLogs.forEach { allLogs.add(it) }
        val logsByAppRunId = allLogs.groupBy { it.appRunId }
        _data.value = allRuns.map { appRun ->
            AppRunWithLogs(
                appRun = appRun,
                logEntries = logsByAppRunId[appRun.id] ?: emptyList()
            )
        }
        val lastKnownAppRunId = allRuns.lastOrNull()?.id ?: lastKnownAppRunId
        val lastKnownLogId = allLogs.lastOrNull()?.id ?: lastKnownLogId
        return lastKnownAppRunId to lastKnownLogId
    }

    fun getUniqueTagsSnapshot(): Set<String> {
        return _data.value.flatMap { it.logEntries }.map { it.tag }.toSet()
    }

    @AssistedFactory
    interface Factory {
        fun create(authority: String): LogRepository
    }
}