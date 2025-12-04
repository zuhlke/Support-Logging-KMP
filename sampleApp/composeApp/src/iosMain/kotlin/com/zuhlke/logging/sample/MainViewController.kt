package com.zuhlke.logging.sample

import androidx.compose.ui.window.ComposeUIViewController
import com.zuhlke.logging.core.repository.AppRunsWithLogsRepository

@Suppress("ktlint:standard:function-naming")
fun MainViewController(logsRepository: AppRunsWithLogsRepository) = ComposeUIViewController {
    App(logsRepository)
}
