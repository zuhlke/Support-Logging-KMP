package com.zuhlke.logging.core.data.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
/**
 * Represents a single log entry in the application.
 *
 * @property id Unique identifier for the log entry.
 * @property timestamp The date and time when the log entry was created as [Instant]
 * @property severity The [Severity] level of the log entry.
 * @property message The log message.
 * @property tag A tag associated with the log entry.
 * @property throwable An optional [Throwable] associated with the log entry.
 * @property appRunId The identifier of the app run to which this log entry belongs.
 */
public data class LogEntry(
    val id: Int,
    val timestamp: Instant,
    val severity: Severity,
    val message: String,
    val tag: String,
    val throwable: String?,
    val appRunId: Int
)
