package com.zuhlke.logging.viewer.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
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

@OptIn(ExperimentalTime::class)
fun LogEntry.snapshot(subsystem: String): LogEntrySnapshot {
    return LogEntrySnapshot(
        date = timestamp,
        level = severity.toIosCompatibleString(),
        subsystem = subsystem,
        category = tag,
        message = message,
        throwable = throwable,
        composedMessage = if (throwable != null) "$message\n$throwable" else message,
    )
}

@Serializable
@OptIn(ExperimentalTime::class)
data class LogEntrySnapshot(
    @Contextual // TODO remove when AS Kotlin plugin is updated
    val date: Instant,
    val level: String,
    val subsystem: String,
    val category: String,
    val message: String,
    val throwable: String?,
    val composedMessage: String
)
