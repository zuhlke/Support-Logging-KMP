package com.zuhlke.logging.core.repository

import com.zuhlke.logging.core.data.model.AppRunWithLogs
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalObjCName::class)
@ObjCName(name = "AppRunsWithLogsRepository", exact = true)
public interface AppRunsWithLogsRepository {
    public fun getLogs(): Flow<List<AppRunWithLogs>>
}
