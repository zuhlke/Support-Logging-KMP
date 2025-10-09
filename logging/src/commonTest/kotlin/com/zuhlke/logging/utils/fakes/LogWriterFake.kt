package com.zuhlke.logging.utils.fakes

import com.zuhlke.logging.LogWriter
import com.zuhlke.logging.data.Severity
import kotlin.time.Instant

class LogWriterFake : LogWriter {

    data class AppRunArgs(
        val launchDate: Instant,
        val appVersion: String,
        val osVersion: String,
        val device: String
    )

    data class LogCallArgs(
        val timestamp: Instant,
        val severity: Severity,
        val message: String,
        val tag: String,
        val throwable: Throwable?
    )

    val writeAppRunArgs = mutableListOf<AppRunArgs>()
    val writeLogCalls = mutableListOf<LogCallArgs>()

    override suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    ) {
        writeAppRunArgs.add(AppRunArgs(launchDate, appVersion, osVersion, device))
    }

    override suspend fun writeLog(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        writeLogCalls.add(LogCallArgs(timestamp, severity, message, tag, throwable))
    }
}