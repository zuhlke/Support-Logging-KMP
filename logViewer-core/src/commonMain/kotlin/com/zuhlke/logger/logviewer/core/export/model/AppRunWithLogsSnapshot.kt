package com.zuhlke.logger.logviewer.core.export.model

import kotlinx.serialization.Serializable

@Serializable
data class AppRunWithLogsSnapshot(val info: AppRunSnapshot, val logEntries: List<LogEntrySnapshot>)
