package com.zuhlke.logging.viewer.ui.details.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zuhlke.logging.viewer.R
import com.zuhlke.logging.viewer.data.model.AppRunWithLogs
import com.zuhlke.logging.viewer.data.model.LogEntry
import com.zuhlke.logging.viewer.data.model.Severity

@Composable
fun AppRunsView(
    appRuns: List<AppRunWithLogs>,
    searchTerm: String,
    showLevel: Boolean,
    showTimestamp: Boolean,
    showTag: Boolean,
    expandLongMessages: Boolean,
    onExportRequested: (List<LogEntry>) -> Unit,
    onSeveritySelected: (Severity) -> Unit,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (appRuns.isEmpty()) {
            Text(
                text = stringResource(R.string.no_logs_yet),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            AppRunsList(
                appRuns = appRuns,
                searchTerm = searchTerm,
                showLevel = showLevel,
                showTimestamp = showTimestamp,
                showTag = showTag,
                expandLongMessages = expandLongMessages,
                onSeveritySelected = onSeveritySelected,
                onTagSelected = onTagSelected,
                onExportRequested = onExportRequested
            )
        }
    }
}
