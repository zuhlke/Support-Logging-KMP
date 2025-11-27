package com.zuhlke.logging.viewer.data.model

import com.zuhlke.logging.viewer.data.model.Severity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class LogEntry(
    val id: Int,
    val timestamp: Instant,
    val severity: Severity,
    val message: String,
    val tag: String,
    val throwable: String?,
    val appRunId: Int
)
