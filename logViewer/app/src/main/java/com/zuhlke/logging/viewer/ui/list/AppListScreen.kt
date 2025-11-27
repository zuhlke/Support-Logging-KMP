package com.zuhlke.logging.viewer.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.zuhlke.logging.viewer.R
import com.zuhlke.logging.viewer.data.GetLogSharingApps
import com.zuhlke.logging.viewer.data.LogSharingApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

typealias Authority = String

@Composable
fun AppListScreen(viewModel: AppListViewModel = hiltViewModel(), onAppClick: (Authority) -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    val apps by viewModel.apps.collectAsStateWithLifecycle()

    AppListScreen(apps, onAppClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(list: List<LogSharingApp>, onAppClick: (Authority) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.app_name))
            },
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .testTag("app_list")
        ) {
            items(list.size) { index ->
                val app = list[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        onAppClick(app.authority)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Name: ${app.name}")
                        Text(text = "Package: ${app.packageName}")
                        Text(text = "Authority: ${app.authority}")
                    }
                }
            }
        }
    }
}

@HiltViewModel
class AppListViewModel @Inject constructor(private val getLoggingAuthorities: GetLogSharingApps) :
    ViewModel() {

    private val _apps = MutableStateFlow<List<LogSharingApp>>(emptyList())
    val apps: StateFlow<List<LogSharingApp>> = _apps

    fun initialize() {
        viewModelScope.launch {
            _apps.emit(getLoggingAuthorities())
        }
    }
}
