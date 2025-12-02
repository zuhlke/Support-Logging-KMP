package com.zuhlke.logger.logviewer.core.export

import com.zuhlke.logging.core.data.model.AppRun
import com.zuhlke.logging.core.data.model.LogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

typealias UriString = String

interface LogExporter {
    suspend fun exportAndShare(appRuns: List<AppRun>, logs: List<LogEntry>): ShareableFile
}

class JsonLogExporter(
    private val converter: JsonLogConverter,
    private val shareService: ShareService
) : LogExporter {
    override suspend fun exportAndShare(appRuns: List<AppRun>, logs: List<LogEntry>): ShareableFile =
        withContext(Dispatchers.IO) {
            val json = converter.convertToJson(appRuns, logs)
            shareService.prepareToShare(json)
        }
}
