package com.zuhlke.logging.viewer.ui.details

import android.net.Uri
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuhlke.logging.viewer.data.AppRunWithLogs
import com.zuhlke.logging.viewer.data.LogEntry
import com.zuhlke.logging.viewer.data.LogRepository
import com.zuhlke.logging.viewer.data.Severity
import com.zuhlke.logging.viewer.export.Exporter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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

@HiltViewModel(assistedFactory = AppDetailsViewModel.Factory::class)
class AppDetailsViewModel @AssistedInject constructor(
    @Assisted val authority: String,
    @Assisted defaultSearchState: SearchState,
    logRepositoryFactory: LogRepository.Factory,
    private val exporter: Exporter
) : ViewModel() {

    private lateinit var collectionJob: Job
    private val logRepository = logRepositoryFactory.create(authority)
    private val logs = MutableStateFlow<List<AppRunWithLogs>>(emptyList())
    private val _selectedSeverities = MutableStateFlow(defaultSearchState.severities)
    val selectedSeverities: StateFlow<Set<Severity>> = _selectedSeverities

    private val _selectedTags = MutableStateFlow(defaultSearchState.tags)
    val selectedTags: StateFlow<Set<String>> = _selectedTags
    private val searchTerm = MutableStateFlow(defaultSearchState.messageText)

    @OptIn(FlowPreview::class)
    private val searchTermDebounce: Flow<String> = searchTerm.debounce(200)

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

    private val _exportReady = MutableSharedFlow<Uri>()
    val exportReady: SharedFlow<Uri> = _exportReady

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
        collectionJob = viewModelScope.launch {
            logRepository.getLogs().collect { appRuns ->
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
            val fileUri = exporter.exportToTempFile(allAppRuns, logEntries)
            _exportReady.emit(fileUri)
        }
    }

    override fun onCleared() {
        super.onCleared()
        collectionJob.cancel()
        Log.d("AppDetailsViewModel", "onCleared")
    }

    @AssistedFactory
    interface Factory {
        fun create(authority: String, defaultSearchState: SearchState): AppDetailsViewModel
    }

    data class UiState(val appRunsWithLogs: List<AppRunWithLogs>, val searchTerm: String)
}
