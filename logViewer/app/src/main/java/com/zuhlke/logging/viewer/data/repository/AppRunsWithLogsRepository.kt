package com.zuhlke.logging.viewer.data.repository

import com.zuhlke.logging.viewer.data.model.AppRunWithLogs
import kotlinx.coroutines.flow.Flow

interface AppRunsWithLogsRepository {
    fun getLogs(): Flow<List<AppRunWithLogs>>
}
