package com.zuhlke.logging.viewer.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.zuhlke.logging.viewer.data.AppRun
import com.zuhlke.logging.viewer.data.LogEntry
import com.zuhlke.logging.viewer.export.model.AppRunWithLogsSnapshot
import com.zuhlke.logging.viewer.export.model.snapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.serialization.json.Json

class Exporter @Inject constructor(@param:ApplicationContext val applicationContext: Context) {

    private val json = Json { prettyPrint = true }

    @OptIn(ExperimentalTime::class)
    fun exportToTempFile(appRuns: List<AppRun>, logs: List<LogEntry>): Uri {
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

        val json = json.encodeToString(toExport)
        val exportFile = File(applicationContext.cacheDir, "exports").apply { mkdir() }
            .resolve("log-${Clock.System.now()}.json")
            .also { it.writeText(json) }
        return FileProvider.getUriForFile(
            /* context = */
            applicationContext,
            /* authority = */
            "com.zuhlke.logging.viewer.fileprovider",
            /* file = */
            exportFile
        )
    }
}
