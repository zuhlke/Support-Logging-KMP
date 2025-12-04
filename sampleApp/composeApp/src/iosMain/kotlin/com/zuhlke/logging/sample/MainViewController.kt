package com.zuhlke.logging.sample

import androidx.compose.ui.window.ComposeUIViewController
import com.zuhlke.logging.core.data.model.AppRunWithLogs
import com.zuhlke.logging.core.repository.AppRunsWithLogsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Suppress("ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController {
    App(object : AppRunsWithLogsRepository {
        override fun getLogs(): Flow<List<AppRunWithLogs>> {
            return emptyFlow()
        }
    })
}
