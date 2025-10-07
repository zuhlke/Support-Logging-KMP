package com.zuhlke.logging.di

import com.zuhlke.logging.RunMetadata

internal class LoggingLibraryContainer(private val factory: LoggingLibraryFactory) {

    val runMetadata: RunMetadata by lazy {
        factory.getMetadata()
    }
}
