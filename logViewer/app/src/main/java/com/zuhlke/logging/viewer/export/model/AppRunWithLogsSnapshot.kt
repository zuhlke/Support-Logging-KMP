package com.zuhlke.logging.viewer.export.model

import kotlinx.serialization.Serializable

@Serializable
data class AppRunWithLogsSnapshot(val info: AppRunSnapshot, val logEntries: List<LogEntrySnapshot>)
