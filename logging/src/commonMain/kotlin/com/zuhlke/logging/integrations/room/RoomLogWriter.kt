package com.zuhlke.logging.integrations.room

import com.zuhlke.logging.LogWriter
import com.zuhlke.logging.Severity
import com.zuhlke.logging.integrations.room.data.AppRun
import com.zuhlke.logging.integrations.room.data.Log
import com.zuhlke.logging.integrations.room.data.LogDao
import kotlin.properties.Delegates
import kotlin.time.Instant

internal class RoomLogWriter(private val logDao: LogDao) : LogWriter() {
    private var appRunId by Delegates.notNull<Int>()

    override suspend fun logAppRun(
        launchDate: Instant, appVersion: String,
        operatingSystemVersion: String,
        device: String
    ) {
        val appRun = AppRun(
            launchDate = launchDate,
            appVersion = appVersion,
            operatingSystemVersion = operatingSystemVersion,
            device = device
        )
        val rowId = logDao.insert(appRun)
        appRunId = logDao.getAppRunIdByRowId(rowId)
    }

    override suspend fun log(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        val log = Log(
            timestamp = timestamp,
            severity = severity,
            message = message,
            tag = tag,
            throwable = throwable?.stackTraceToString(),
            appRunId = appRunId
        )
        logDao.insert(log)
    }
}