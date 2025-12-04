package com.zuhlke.logger.logviewer.core.ui

import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zuhlke.logger.logviewer.core.export.JsonLogExporter
import com.zuhlke.logger.logviewer.core.export.KotlinSerializationJsonLogConverter
import com.zuhlke.logger.logviewer.core.export.LogExporter
import com.zuhlke.logger.logviewer.core.export.ShareableFile
import com.zuhlke.logging.core.data.model.AppRunWithLogs
import com.zuhlke.logging.core.data.model.LogEntry
import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.core.repository.AppRunsWithLogsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class AppDetailsViewModel(
    defaultSearchState: SearchState,
    private val repository: AppRunsWithLogsRepository,
    private val logExporter: LogExporter
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val logs = MutableStateFlow<List<AppRunWithLogs>>(emptyList())
    private val _selectedSeverities = MutableStateFlow(defaultSearchState.severities)
    val selectedSeverities: StateFlow<Set<Severity>> = _selectedSeverities

    private val _selectedTags = MutableStateFlow(defaultSearchState.tags)
    val selectedTags: StateFlow<Set<String>> = _selectedTags
    private val searchTerm = MutableStateFlow(defaultSearchState.messageText)

    @OptIn(FlowPreview::class)
    private val searchTermDebounce: Flow<String> = searchTerm.debounce(200)

    private val _exportReady = MutableSharedFlow<ShareableFile>()
    val exportReady: SharedFlow<ShareableFile> = _exportReady

    // TODO: shall we expose tags and severities here too instead of having separate public properties for them?
    val filteredAppRuns: StateFlow<UiState> = combine(
        logs,
        _selectedSeverities,
        _selectedTags,
        searchTermDebounce
    ) { appRuns, severities, tags, searchTermDebounced ->
        if (severities.isEmpty() && tags.isEmpty() && searchTermDebounced == "") {
            UiState(appRuns, searchTerm = "")
        } else {
            val regex = Regex(searchTermDebounced)
            UiState(
                appRuns.map { appRun ->
                    val filteredEntries = appRun.logEntries
                        .filter { severities.isEmpty() || it.severity in severities }
                        .filter { tags.isEmpty() || it.tag in tags }
                        .filter { searchTermDebounced == "" || regex.containsMatchIn(it.message) }
                    appRun.copy(logEntries = filteredEntries)
                }.filter { it.logEntries.isNotEmpty() },
                searchTermDebounced
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        UiState(emptyList(), searchTerm = "")
    )

    private val _uniqueTags: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val uniqueTags: StateFlow<Set<String>> = _uniqueTags

    fun setSeverities(severities: Set<Severity>) {
        viewModelScope.launch {
            _selectedSeverities.emit(severities.toSet())
        }
    }

    fun setTags(tags: Set<String>) {
        viewModelScope.launch {
            _selectedTags.emit(tags.toSet())
        }
    }

    fun setSearchTerm(searchTerm: String) {
        viewModelScope.launch {
            this@AppDetailsViewModel.searchTerm.emit(searchTerm)
        }
    }

    private var initialised = false

    @OptIn(ExperimentalCoroutinesApi::class)
    @MainThread
    fun init() {
        if (initialised) return
        initialised = true
        viewModelScope.launch {
            repository.getLogs().collect { appRuns ->
                logs.value = appRuns.filter { runWithLogs -> runWithLogs.logEntries.isNotEmpty() }

                val tags = logs.value.flatMap { it.logEntries.map { entry -> entry.tag } }.toSet()
                _uniqueTags.emit(tags)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun export(logEntries: List<LogEntry>) {
        viewModelScope.launch {
            val allAppRuns = logs.value.map { it.appRun }
            val readyFile = logExporter.exportAndShare(allAppRuns, logEntries)
            _exportReady.emit(readyFile)
        }
    }

    fun clear() {
        viewModelScope.coroutineContext.cancelChildren()
        println("AppDetailsViewModel clear called") // TODO: remove
    }

    data class UiState(val appRunsWithLogs: List<AppRunWithLogs>, val searchTerm: String)
}
