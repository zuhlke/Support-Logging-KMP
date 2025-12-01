package com.zuhlke.logging.core.repository

import com.zuhlke.logging.core.data.model.AppRunWithLogs
import kotlinx.coroutines.flow.Flow

public interface AppRunsWithLogsRepository {
    public fun getLogs(): Flow<List<AppRunWithLogs>>
}
