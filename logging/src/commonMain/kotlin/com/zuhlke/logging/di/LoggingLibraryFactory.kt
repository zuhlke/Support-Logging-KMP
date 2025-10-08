package com.zuhlke.logging.di

import com.zuhlke.logging.data.RunMetadata

internal interface LoggingLibraryFactory {
    fun getMetadata(): RunMetadata
}
