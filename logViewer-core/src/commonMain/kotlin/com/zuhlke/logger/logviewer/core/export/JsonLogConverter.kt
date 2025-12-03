package com.zuhlke.logger.logviewer.core.export

import com.zuhlke.logging.core.data.model.AppRun
import com.zuhlke.logging.core.data.model.LogEntry

internal interface JsonLogConverter {
    fun convertToJson(appRuns: List<AppRun>, logs: List<LogEntry>): String
}
