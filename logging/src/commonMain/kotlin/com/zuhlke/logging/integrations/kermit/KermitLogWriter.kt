package com.zuhlke.logging.integrations.kermit

import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import com.zuhlke.logging.core.LogWriter
import com.zuhlke.logging.core.data.model.Severity
import kotlin.time.Instant

internal class KermitLogWriter(subsystem: String) : LogWriter {

    private val logger = Logger(loggerConfigInit(platformLogWriter(subsystem)))

    override suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    ) {
        logger.log(
            severity = Severity.Info.toKermitSeverity(),
            tag = "AppRun",
            message = "App started. Version: $appVersion, OS: $osVersion, Device: $device",
            throwable = null
        )
    }

    override suspend fun writeLog(
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

/**
 * Maps Zuhlke Logging Severity to Kermit Severity.
 */
public fun Severity.toKermitSeverity(): co.touchlab.kermit.Severity = when (this) {
    Severity.Verbose -> co.touchlab.kermit.Severity.Verbose
    Severity.Debug -> co.touchlab.kermit.Severity.Debug
    Severity.Info -> co.touchlab.kermit.Severity.Info
    Severity.Warn -> co.touchlab.kermit.Severity.Warn
    Severity.Error -> co.touchlab.kermit.Severity.Error
    Severity.Assert -> co.touchlab.kermit.Severity.Assert
}

internal expect fun platformLogWriter(subsystem: String): co.touchlab.kermit.LogWriter
