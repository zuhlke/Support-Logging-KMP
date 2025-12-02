package com.zuhlke.logging.integration.room.repository

import com.zuhlke.logging.core.data.model.AppRunWithLogs
import com.zuhlke.logging.core.data.model.LogEntry
import com.zuhlke.logging.core.repository.AppRunsWithLogsRepository
import com.zuhlke.logging.integration.room.data.AppRun
import com.zuhlke.logging.integration.room.data.Log
import com.zuhlke.logging.integration.room.data.LogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

public class RoomAppRunWithLogsRepository internal constructor(private val logDao: LogDao) :
    AppRunsWithLogsRepository {

    override fun getLogs(): Flow<List<AppRunWithLogs>> {
        return combine(logDao.getAllAppRuns(), logDao.getAllLogs()) { appRuns, logs ->
            appRuns.map { appRun ->
                val associatedLogs = logs.filter { it.appRunId == appRun.id }
                AppRunWithLogs(
                    appRun = appRun.toModel(),
                    logEntries = associatedLogs.map(Log::toModel)
                )
            }
        }
    }
}

private fun AppRun.toModel(): com.zuhlke.logging.core.data.model.AppRun {
    return com.zuhlke.logging.core.data.model.AppRun(
        id = this.id,
        launchDate = this.launchDate,
        appVersion = this.appVersion,
        osVersion = this.operatingSystemVersion,
        device = this.device
    )
}

private fun Log.toModel(): LogEntry {
    return LogEntry(
        id = this.id,
        timestamp = this.timestamp,
        severity = this.severity,
        message = this.message,
        tag = this.tag,
        throwable = this.throwable,
        appRunId = this.appRunId
    )
}