package com.zuhlke.logging.integration.room

import com.zuhlke.logging.core.LogWriter
import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.integration.room.data.AppRun
import com.zuhlke.logging.integration.room.data.Log
import com.zuhlke.logging.integration.room.data.LogDao
import kotlin.properties.Delegates
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class RoomLogWriter(private val logDao: LogDao) : LogWriter {
    private var appRunId by Delegates.notNull<Int>()

    override suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    ) {
        val appRun = AppRun(
            launchDate = launchDate,
            appVersion = appVersion,
            operatingSystemVersion = osVersion,
            device = device
        )
        val rowId = logDao.insert(appRun)
        appRunId = logDao.getAppRunIdByRowId(rowId)
    }

    override suspend fun writeLog(
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
