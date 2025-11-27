package com.zuhlke.logging.viewer.export.model

import com.zuhlke.logging.viewer.data.model.LogEntry
import com.zuhlke.logging.viewer.data.model.Severity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
<<<<<<<< HEAD:logViewer/app/src/main/java/com/zuhlke/logging/viewer/export/model/LogEntrySnapshot.kt
import kotlinx.serialization.Serializable
========
>>>>>>>> origin/main:logViewer/app/src/main/java/com/zuhlke/logging/viewer/data/LogEntry.kt

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
<<<<<<<< HEAD:logViewer/app/src/main/java/com/zuhlke/logging/viewer/export/model/LogEntrySnapshot.kt

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
========
>>>>>>>> origin/main:logViewer/app/src/main/java/com/zuhlke/logging/viewer/data/LogEntry.kt
