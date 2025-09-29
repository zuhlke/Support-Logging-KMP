package com.zuhlke.logging.di

import com.zuhlke.logging.RunMetadata
import com.zuhlke.logging.data.LogDao

// TODO: hide this class from external usage
internal class LoggingLibraryContainer(private val factory: LoggingLibraryFactory) {

    val logDao: LogDao by lazy {
        factory.createLogRoomDatabase().logDao()
    }

    val runMetadata: RunMetadata by lazy {
        factory.getMetadata()
    }
}
