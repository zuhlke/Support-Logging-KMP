package com.zuhlke.logging.viewer.ui.details.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.viewer.data.model.AppRunWithLogs
import com.zuhlke.logging.viewer.data.model.LogEntry

@Composable
fun AppRunsList(
    appRuns: List<AppRunWithLogs>,
    searchTerm: String,
    showLevel: Boolean,
    showTimestamp: Boolean,
    showTag: Boolean,
    expandLongMessages: Boolean,
    onExportRequested: (List<LogEntry>) -> Unit,
    onSeveritySelected: (Severity) -> Unit,
    onTagSelected: (String) -> Unit
) {
    // TODO: add heuristic to decide whether to scroll to bottom or not
    // e.g. if user is at the bottom, scroll to bottom on new messages,
    // but if user has scrolled up, don't scroll to bottom automatically
    val scrollState = rememberLazyListState()
    LaunchedEffect(appRuns) {
        scrollState.scrollToItem(scrollState.layoutInfo.totalItemsCount)
    }

    // TODO: validate pattern before using it for regex creation
    val highlightsRegex = remember(searchTerm) {
        Regex(searchTerm)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("log_list"),
        state = scrollState
    ) {
        for (appRun in appRuns) {
            stickyHeader(key = "appRun-${appRun.appRun.id}") {
                AppRunHeader(
                    appRun.appRun,
                    onExport = {
                        onExportRequested(appRun.logEntries)
                    },
                    modifier = Modifier.animateItem()
                )
            }
            items(appRun.logEntries, key = { it.id }) { entry ->
                LogRowWithContextMenu(
                    entry = entry,
                    highlightsRegex = highlightsRegex,
                    showLevel = showLevel,
                    showTimestamp = showTimestamp,
                    showTag = showTag,
                    expandedByDefault = expandLongMessages,
                    onExportRequested = { onExportRequested(listOf(entry)) },
                    onSeveritySelected = {
                        onSeveritySelected(entry.severity)
                    },
                    onTagSelected = {
                        onTagSelected(entry.tag)
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
