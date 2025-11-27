package com.zuhlke.logging.viewer.export.model

import com.zuhlke.logging.viewer.data.LogEntry
import com.zuhlke.logging.viewer.data.Severity
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Serializable
@OptIn(ExperimentalTime::class)
data class LogEntrySnapshot(
    val date: Instant,
    val level: String,
    val subsystem: String,
    val category: String,
    val message: String,
    val throwable: String?,
    val composedMessage: String
)

@OptIn(ExperimentalTime::class)
fun LogEntry.snapshot(subsystem: String): LogEntrySnapshot = LogEntrySnapshot(
    date = timestamp,
    level = severity.toIosCompatibleString(),
    subsystem = subsystem,
    category = tag,
    message = message,
    throwable = throwable,
    composedMessage = if (throwable != null) "$message\n$throwable" else message
)

fun Severity.toIosCompatibleString(): String = when (this) {
    Severity.Verbose -> "verbose"
    Severity.Debug -> "debug"
    Severity.Info -> "info"
    Severity.Warn -> "notice"
    Severity.Error -> "error"
    Severity.Assert -> "fault"
}