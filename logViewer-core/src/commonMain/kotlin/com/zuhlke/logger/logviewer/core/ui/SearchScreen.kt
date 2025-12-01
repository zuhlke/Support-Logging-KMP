package com.zuhlke.logger.logviewer.core.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zuhlke.logger.logviewer.core.ui.utils.stringResource
import com.zuhlke.logger.logviewer.core.ui.widgets.AppRunsView
import com.zuhlke.logger.logviewer.core.ui.widgets.SeverityModalBottomSheet
import com.zuhlke.logger.logviewer.core.ui.widgets.TopAppBarWithTitle
import com.zuhlke.logging.core.data.model.AppRunWithLogs
import com.zuhlke.logging.core.data.model.LogEntry
import com.zuhlke.logging.core.data.model.Severity
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.filter_severity
import supportloggingkmp.logviewer_core.generated.resources.filter_tags
import supportloggingkmp.logviewer_core.generated.resources.ic_arrow_drop_down
import supportloggingkmp.logviewer_core.generated.resources.search

@Composable
fun SearchScreen(
    viewModel: AppDetailsViewModel,
    onTagSelectorRequested: (TagFilterState) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.init()
    }

    val appRuns by viewModel.filteredAppRuns.collectAsStateWithLifecycle()
    val selectedSeverities by viewModel.selectedSeverities.collectAsStateWithLifecycle()
    val uniqueTags by viewModel.uniqueTags.collectAsStateWithLifecycle()
    val selectedTags by viewModel.selectedTags.collectAsStateWithLifecycle()

    SearchScreen(
        appRuns = appRuns.appRunsWithLogs,
        searchTerm = appRuns.searchTerm,
        onExportRequested = viewModel::export,
        selectedSeverities = selectedSeverities,
        onSelectedSeveritiesChanged = viewModel::setSeverities,
        selectedTags = selectedTags,
        onTagsClick = {
            onTagSelectorRequested(
                TagFilterState(
                    selectedTags = selectedTags,
                    allTags = uniqueTags
                )
            )
        },
        onSelectedTagsChanged = viewModel::setTags,
        onSearch = viewModel::setSearchTerm,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    appRuns: List<AppRunWithLogs>,
    searchTerm: String,
    onExportRequested: (List<LogEntry>) -> Unit,
    selectedSeverities: Set<Severity>,
    onSelectedSeveritiesChanged: (Set<Severity>) -> Unit,
    selectedTags: Set<String>,
    onSelectedTagsChanged: (Set<String>) -> Unit,
    onTagsClick: () -> Unit,
    onSearch: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    SearchScreen(
        title = {
            SearchBar(
                modifier = Modifier
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchText,
                        onQueryChange = {
                            searchText = it
                            onSearch(it)
                        },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text(stringResource(Res.string.search)) }
                    )
                },
                expanded = false,
                onExpandedChange = { }
            ) { }
        },
        appRuns = appRuns,
        searchTerm = searchTerm,
        selectedSeverities = selectedSeverities,
        onSelectedSeveritiesChanged = onSelectedSeveritiesChanged,
        selectedTags = selectedTags,
        onSelectedTagsChanged = onSelectedTagsChanged,
        onTagsClick = onTagsClick,
        onExportRequested = onExportRequested,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    title: @Composable (() -> Unit),
    appRuns: List<AppRunWithLogs>,
    searchTerm: String,
    selectedSeverities: Set<Severity>,
    onSelectedSeveritiesChanged: (Set<Severity>) -> Unit,
    selectedTags: Set<String>,
    onSelectedTagsChanged: (Set<String>) -> Unit,
    onTagsClick: () -> Unit,
    onExportRequested: (List<LogEntry>) -> Unit,
    onBack: () -> Unit
) {
    var showLevel by rememberSaveable { mutableStateOf(true) }
    var showTimestamp by rememberSaveable { mutableStateOf(true) }
    var showTag by rememberSaveable { mutableStateOf(true) }
    var expandLongMessages by rememberSaveable { mutableStateOf(false) }

    var showSeverityModalSheet by rememberSaveable { mutableStateOf(false) }
    val severityChipSelected = selectedSeverities.isNotEmpty()
    val severityChipText = getSeverityChipText(selectedSeverities)

    val tagChipSelected = selectedTags.isNotEmpty()
    val tagChipText = getTagChipText(selectedTags)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                Row(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    FilterChip(
                        selected = severityChipSelected,
                        onClick = { showSeverityModalSheet = true },
                        trailingIcon = {
                            Icon(
                                painterResource(Res.drawable.ic_arrow_drop_down),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(severityChipText)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = tagChipSelected,
                        onClick = onTagsClick,
                        trailingIcon = {
                            Icon(
                                painterResource(Res.drawable.ic_arrow_drop_down),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(tagChipText)
                        }
                    )
                }
                AppRunsView(
                    appRuns = appRuns,
                    searchTerm = searchTerm,
                    showLevel = showLevel,
                    showTimestamp = showTimestamp,
                    showTag = showTag,
                    expandLongMessages = expandLongMessages,
                    onExportRequested = onExportRequested,
                    onSeveritySelected = { severity ->
                        onSelectedSeveritiesChanged(selectedSeverities + severity)
                    },
                    onTagSelected = { tag ->
                        onSelectedTagsChanged(selectedTags + tag)
                    }
                )
            }
            if (showSeverityModalSheet) {
                SeverityModalBottomSheet(
                    onDismissRequest = { showSeverityModalSheet = false },
                    selectedSeverities = selectedSeverities,
                    onSelectionChanged = onSelectedSeveritiesChanged
                )
            }
        }
    }
}

@Composable
fun getTagChipText(selectedTags: Set<String>): String = if (selectedTags.isEmpty()) {
    stringResource(Res.string.filter_tags)
} else {
    selectedTags.minOf { it } + if (selectedTags.size > 1) {
        "+${selectedTags.size - 1}"
    } else {
        ""
    }
}

@Composable
private fun getSeverityChipText(selectedSeverities: Set<Severity>): String =
    if (selectedSeverities.isEmpty()) {
        stringResource(Res.string.filter_severity)
    } else {
        val firstSelected = Severity.entries.first { it in selectedSeverities }.stringResource
        stringResource(firstSelected) + if (selectedSeverities.size > 1) {
            "+${selectedSeverities.size - 1}"
        } else {
            ""
        }
    }
