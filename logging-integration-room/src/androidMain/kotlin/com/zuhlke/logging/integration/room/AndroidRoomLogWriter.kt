package com.zuhlke.logging.integration.room

import android.content.Context
import com.zuhlke.logging.core.LogWriter
import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.integration.room.data.LogDatabase
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
public class AndroidRoomLogWriter(context: Context): LogWriter {
    private val applicationContext = context.applicationContext

    internal val logDatabase: LogDatabase by lazy {
        createLogRoomDatabase(applicationContext)
    }

    private val roomLogWriter: LogWriter by lazy {
        RoomLogWriter(logDatabase.logDao())
    }

    override suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    ) {
        roomLogWriter.writeAppRun(launchDate, appVersion, osVersion, device)
    }

    override suspend fun writeLog(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        roomLogWriter.writeLog(timestamp, severity, message, tag, throwable)
    }
}