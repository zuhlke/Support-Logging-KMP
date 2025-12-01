package com.zuhlke.logger.logviewer.core.export

import com.zuhlke.logger.logviewer.core.export.model.AppRunWithLogsSnapshot
import com.zuhlke.logger.logviewer.core.export.model.snapshot
import com.zuhlke.logging.core.data.model.AppRun
import com.zuhlke.logging.core.data.model.LogEntry
import kotlinx.serialization.json.Json

class KotlinSerializationJsonLogConverter : JsonLogConverter {

    val json = Json { prettyPrint = true }

    override fun convertToJson(appRuns: List<AppRun>, logs: List<LogEntry>): String {
        val logsByAppRun =
            appRuns.associateWith { appRun -> logs.filter { it.appRunId == appRun.id } }
                .filter { it.value.isNotEmpty() }


        val toExport = logsByAppRun.map { (appRun, logs) ->
            AppRunWithLogsSnapshot(
                info = appRun.snapshot,
                logEntries = logs.map {
                    // TODO: Make subsystem configurable
                    it.snapshot(subsystem = "com.zuhlke.logging")
                }
            )
        }

        return json.encodeToString(toExport)
    }
}