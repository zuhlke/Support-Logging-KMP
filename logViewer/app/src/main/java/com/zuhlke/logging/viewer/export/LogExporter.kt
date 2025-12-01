package com.zuhlke.logging.viewer.export

import android.net.Uri
import com.zuhlke.logging.core.data.model.AppRun
import com.zuhlke.logging.core.data.model.LogEntry

interface LogExporter {
    fun exportToShareableFile(appRuns: List<AppRun>, logs: List<LogEntry>): Uri
}
