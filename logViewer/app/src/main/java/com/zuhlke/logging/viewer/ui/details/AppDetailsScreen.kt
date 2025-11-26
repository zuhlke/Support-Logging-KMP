@file:OptIn(ExperimentalTime::class)

package com.zuhlke.logging.viewer.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zuhlke.logging.viewer.R
import com.zuhlke.logging.viewer.data.AppRun
import com.zuhlke.logging.viewer.data.AppRunWithLogs
import com.zuhlke.logging.viewer.data.LogEntry
import com.zuhlke.logging.viewer.data.Severity
import com.zuhlke.logging.viewer.ui.details.widgets.AppRunsView
import com.zuhlke.logging.viewer.ui.details.widgets.TopAppBarWithTitle
import com.zuhlke.logging.viewer.ui.theme.LogsViewerTheme
import com.zuhlke.logging.viewer.ui.utils.startShare
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun AppDetailsScreen(
    viewModel: AppDetailsViewModel,
    onSearch: (SearchState) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.init()
    }

    val appRuns by viewModel.filteredAppRuns.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.exportReady.collect { uri ->
            context.startShare(uri)
        }
    }
    AppDetailsScreen(
        appRuns = appRuns.appRunsWithLogs,
        searchTerm = appRuns.searchTerm,
        onExportRequested = viewModel::export,
        onSearch = onSearch,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDetailsScreen(
    appRuns: List<AppRunWithLogs>,
    searchTerm: String,
    onExportRequested: (List<LogEntry>) -> Unit,
    onSearch: (SearchState) -> Unit,
    onBack: () -> Unit
) {
    val textFieldState = rememberTextFieldState()
    AppDetailsScreen(
        title = {
            SimpleSearchBar(
                textFieldState,
                onSearch = { onSearch(SearchState()) },
                searchResults = emptyList(),
                Modifier
            )
        },
        appRuns = appRuns,
        searchTerm = searchTerm,
        onExportRequested = onExportRequested,
        onSeveritySelected = { severity ->
            onSearch(SearchState(severities = setOf(severity)))
        },
        onTagSelected = { tag ->
            onSearch(SearchState(tags = setOf(tag)))
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDetailsScreen(
    title: @Composable (() -> Unit),
    appRuns: List<AppRunWithLogs>,
    searchTerm: String,
    onExportRequested: (List<LogEntry>) -> Unit,
    onSeveritySelected: (Severity) -> Unit,
    onTagSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var showLevel by rememberSaveable { mutableStateOf(true) }
    var showTimestamp by rememberSaveable { mutableStateOf(true) }
    var showTag by rememberSaveable { mutableStateOf(true) }
    var expandLongMessages by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarWithTitle(
                title = title,
                showLevel = showLevel,
                showTimestamp = showTimestamp,
                showTag = showTag,
                expandLongMessages = expandLongMessages,
                onShowLevelChanged = {
                    showLevel = it
                },
                onShowTimestampChanged = {
                    showTimestamp = it
                },
                onShowTagChanged = {
                    showTag = it
                },
                onExpandLongMessagesChanged = {
                    expandLongMessages = it
                },
                onExport = {
                    val allEntries = appRuns.flatMap { it.logEntries }
                    onExportRequested(allEntries)
                },
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        AppRunsView(
            appRuns = appRuns,
            searchTerm = searchTerm,
            showLevel = showLevel,
            showTimestamp = showTimestamp,
            showTag = showTag,
            expandLongMessages = expandLongMessages,
            onExportRequested = onExportRequested,
            onSeveritySelected = onSeveritySelected,
            onTagSelected = onTagSelected,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    textFieldState: TextFieldState,
    onSearch: () -> Unit,
    searchResults: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = { },
                    expanded = expanded,
                    onExpandedChange = { onSearch() },
                    placeholder = { Text(stringResource(R.string.search)) }
                )
            },
            expanded = expanded,
            onExpandedChange = { },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                searchResults.forEach { result ->
                    ListItem(
                        headlineContent = { Text(result) },
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result) }
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppDetailsPreview() {
    LogsViewerTheme {
        AppDetailsScreen(
            onBack = {},
            onExportRequested = {},
            onSearch = {},
            appRuns = listOf(
                AppRunWithLogs(
                    appRun = AppRun(
                        id = 1,
                        launchDate = Instant.parse("2023-12-18T12:00:00Z"),
                        appVersion = "1.0.0",
                        osVersion = "Android 12",
                        device = "Pixel 5"
                    ),
                    logEntries = listOf(
                        LogEntry(
                            id = 1,
                            timestamp = Instant.parse("2023-10-01T12:34:56Z"),
                            severity = Severity.Verbose,
                            message = "This a sample verbose log message",
                            tag = "SampleTag1",
                            throwable = null,
                            appRunId = 1
                        ),
                        LogEntry(
                            id = 2,
                            timestamp = Instant.parse("2023-10-01T12:34:56Z"),
                            severity = Severity.Debug,
                            message = "This is a sample log message. Very long message to test wrapping and see how it looks in the UI. This should be truncated if not expanded. Let's add even more text to ensure it exceeds two lines in the display. And even more text to be sure!",
                            tag = "SampleTag",
                            throwable = "java.lang.Exception: Sample exception",
                            appRunId = 1
                        ),
                        LogEntry(
                            id = 3,
                            timestamp = Instant.parse("2023-10-01T12:34:57Z"),
                            severity = Severity.Info,
                            message = "This is a sample info message",
                            tag = "SampleTag2",
                            throwable = "java.lang.Exception: Sample exception",
                            appRunId = 1
                        ),
                        LogEntry(
                            id = 4,
                            timestamp = Instant.parse("2023-10-01T12:34:57Z"),
                            severity = Severity.Warn,
                            message = "This is a sample warning message",
                            tag = "SampleTag2",
                            throwable = null,
                            appRunId = 1
                        ),
                        LogEntry(
                            id = 5,
                            timestamp = Instant.parse("2023-10-01T12:34:57Z"),
                            severity = Severity.Error,
                            message = "This is a sample error message",
                            tag = "SampleTag2",
                            throwable = "java.lang.Exception: Sample exception",
                            appRunId = 1
                        ),
                        LogEntry(
                            id = 6,
                            timestamp = Instant.parse("2023-10-01T12:34:57Z"),
                            severity = Severity.Assert,
                            message = "This is a sample assertion message",
                            tag = "SampleTag2",
                            throwable = "java.lang.Exception: Sample exception",
                            appRunId = 1
                        )
                    )
                )
            ),
            searchTerm = "This"
        )
    }
}

