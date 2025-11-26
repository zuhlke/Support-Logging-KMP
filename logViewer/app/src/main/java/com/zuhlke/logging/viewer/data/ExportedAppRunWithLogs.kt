package com.zuhlke.logging.viewer.data

import kotlinx.serialization.Serializable

@Serializable
data class ExportedAppRunWithLogs(
    val info: AppRunSnapshot,
    val logEntries: List<LogEntrySnapshot>
)