package com.zuhlke.logging.di

import com.zuhlke.logging.RunMetadata

internal interface LoggingLibraryFactory {
    fun getMetadata(): RunMetadata
}
