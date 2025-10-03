package com.zuhlke.logging

import kotlin.time.Instant

internal abstract class LogWriter {

    abstract suspend fun logAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    )

    abstract suspend fun log(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable? = null
    )
}
