package com.zuhlke.logger.logviewer.core.export

import com.zuhlke.logging.core.data.model.AppRun
import com.zuhlke.logging.core.data.model.LogEntry

class LogExporter(private val converter: JsonLogConverter, private val shareService: ShareService) {
    suspend fun exportAndShare(appRuns: List<AppRun>, logs: List<LogEntry>) {
        val json = converter.convertToJson(appRuns, logs)
        shareService.share(json)
    }
}
