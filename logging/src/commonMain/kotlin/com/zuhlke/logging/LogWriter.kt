package com.zuhlke.logging

import com.zuhlke.logging.data.Severity
import kotlin.time.Instant

internal interface LogWriter {

    suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    )

    suspend fun writeLog(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable? = null
    )
}
