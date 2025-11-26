package com.zuhlke.logging.viewer.ui.details

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuhlke.logging.viewer.data.AppRunWithLogs
import com.zuhlke.logging.viewer.data.ExportedAppRunWithLogs
import com.zuhlke.logging.viewer.data.LogEntry
import com.zuhlke.logging.viewer.data.LogRepository
import com.zuhlke.logging.viewer.data.Severity
import com.zuhlke.logging.viewer.data.snapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@HiltViewModel(assistedFactory = AppDetailsViewModel.Factory::class)
class AppDetailsViewModel @AssistedInject constructor(
    @Assisted val authority: String,
    @Assisted val defaultSearchState: SearchState,
    logRepositoryFactory: LogRepository.Factory,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val logRepository = logRepositoryFactory.create(authority)
    private val logs = MutableStateFlow<List<AppRunWithLogs>>(emptyList())
    private val _selectedSeverities = MutableStateFlow(defaultSearchState.severities)
    val selectedSeverities: StateFlow<Set<Severity>> = _selectedSeverities

    private val _selectedTags = MutableStateFlow(defaultSearchState.tags)
    val selectedTags: StateFlow<Set<String>> = _selectedTags
    private val _searchTerm = MutableStateFlow(defaultSearchState.messageText)

    @OptIn(FlowPreview::class)
    private val _searchTermDebounce: Flow<String> = _searchTerm.debounce(200)

    // TODO: shall we expose tags and severities here too instead of having separate public properties for them?
    val filteredAppRuns: StateFlow<UiState> = combine(
        logs,
        _selectedSeverities,
        _selectedTags,
        _searchTermDebounce
    ) { appRuns, severities, tags, searchTermDebounced ->
        if (severities.isEmpty() && tags.isEmpty() && searchTermDebounced == "") {
            UiState(appRuns, "")
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
        viewModelScope, SharingStarted.Lazily, UiState(emptyList(), searchTerm = "")
    )

    private val _exportReady = MutableSharedFlow<Uri>()
    val exportReady: SharedFlow<Uri> = _exportReady

    private val autoRefresh = MutableStateFlow(true)
    private var lastKnownAppRunId = -1
    private var lastKnownLogId = -1

    fun setAutoRefresh(enabled: Boolean) {
        autoRefresh.value = enabled
    }

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
            _searchTerm.emit(searchTerm)
        }
    }

    private var initialised = false

    @OptIn(ExperimentalCoroutinesApi::class)
    @MainThread
    fun init() {
        if (initialised) return
        initialised = true
        viewModelScope.launch {
            autoRefresh.transformLatest { enabled ->
                if (enabled) {
                    while (true) {
                        emit(Unit)
                        delay(1000)
                    }
                }
            }.collect {
                val result = logRepository.fetch(
                    lastKnownAppRunId = lastKnownAppRunId,
                    lastKnownLogId = lastKnownLogId
                )
                lastKnownAppRunId = result.first
                lastKnownLogId = result.second
            }
        }
        viewModelScope.launch {
            logRepository.data.collect { appRuns ->
                logs.value = appRuns.filter { it.logEntries.isNotEmpty() }
            }
        }
    }

    // TODO: viewmodel shouldn't expose functions that return something
    fun getUniqueTags(): Set<String> {
        return logRepository.getUniqueTagsSnapshot()
    }

    private val json = Json { prettyPrint = true }

    @OptIn(ExperimentalTime::class)
    fun export(logEntries: List<LogEntry>) {
        viewModelScope.launch {
            val file = withContext(Dispatchers.IO) {
                val toExport = logEntries.map { it.appRunId }.toSet().map { appRunId ->
                    val appRunWithLogs = logs.value.first { it.appRun.id == appRunId }
                    val appRunEntries = logEntries.filter { it.appRunId == appRunId }
                    ExportedAppRunWithLogs(
                        info = appRunWithLogs.appRun.snapshot,
                        logEntries = appRunEntries.map { it.snapshot(authority.removePrefix(".logging")) }
                    )
                }
                val json = json.encodeToString(toExport)
                File(context.filesDir, "exports").apply { mkdir() }
                    .resolve("log-${Clock.System.now()}.json")
                    .also { it.writeText(json) }
            }
            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "com.zuhlke.logging.viewer.fileprovider",
                file
            )
            _exportReady.emit(fileUri)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("AppDetailsViewModel", "onCleared")
    }

    @AssistedFactory
    interface Factory {
        fun create(authority: String, defaultSearchState: SearchState): AppDetailsViewModel
    }

    data class UiState(
        val appRunsWithLogs: List<AppRunWithLogs>,
        val searchTerm: String
    )
}

