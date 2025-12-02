package com.zuhlke.logging.core

import com.zuhlke.logging.core.data.model.Severity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
public interface LogWriter {

    public suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    )

    public suspend fun writeLog(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable? = null
    )
}