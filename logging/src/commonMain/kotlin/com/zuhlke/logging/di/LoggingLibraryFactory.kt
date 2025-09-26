package com.zuhlke.logging.di

import com.zuhlke.logging.RunMetadata
import com.zuhlke.logging.data.LogDatabase

internal interface LoggingLibraryFactory {
    fun createLogRoomDatabase(): LogDatabase

    fun getMetadata(): RunMetadata
}

internal const val LOG_DB_FILENAME = "logs.db"

