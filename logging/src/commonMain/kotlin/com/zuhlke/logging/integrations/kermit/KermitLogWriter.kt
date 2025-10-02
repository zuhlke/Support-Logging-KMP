package com.zuhlke.logging.integrations.kermit

import co.touchlab.kermit.Logger
import co.touchlab.kermit.SimpleFormatter
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import com.zuhlke.logging.LogWriter
import com.zuhlke.logging.Severity
import kotlin.time.Instant

internal class KermitLogWriter : LogWriter() {

    private val logger = Logger(loggerConfigInit(platformLogWriter(SimpleFormatter)))

    override suspend fun logAppRun(
        launchDate: Instant,
        appVersion: String,
        operatingSystemVersion: String,
        device: String
    ) {
        logger.log(
            severity = Severity.Info.toKermitSeverity(),
            tag = "AppRun",
            message = "App started. Version: $appVersion, OS: $operatingSystemVersion, Device: $device",
            throwable = null
        )
    }

    override suspend fun log(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        logger.log(
            severity = severity.toKermitSeverity(),
            tag = tag,
            throwable = throwable,
            message = message
        )
    }
}

internal fun Severity.toKermitSeverity(): co.touchlab.kermit.Severity = when (this) {
    Severity.Verbose -> co.touchlab.kermit.Severity.Verbose
    Severity.Debug -> co.touchlab.kermit.Severity.Debug
    Severity.Info -> co.touchlab.kermit.Severity.Info
    Severity.Warn -> co.touchlab.kermit.Severity.Warn
    Severity.Error -> co.touchlab.kermit.Severity.Error
    Severity.Assert -> co.touchlab.kermit.Severity.Assert
}