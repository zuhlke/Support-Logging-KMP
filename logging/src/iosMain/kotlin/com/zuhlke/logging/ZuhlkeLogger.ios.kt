package com.zuhlke.logging

import com.zuhlke.logging.InterpolationConfiguration.SafeInterpolation
import com.zuhlke.logging.InterpolationConfiguration.UnsafeInterpolation
import com.zuhlke.logging.di.IosLoggingLibraryFactory
import com.zuhlke.logging.di.LoggingLibraryContainer
import com.zuhlke.logging.integrations.kermit.KermitLogWriter
import kotlin.time.Clock

public actual object ZuhlkeLogger {
    public fun initialize(useSafeInterpolation: Boolean) {
        val interpolationConfiguration = if (useSafeInterpolation) {
            SafeInterpolation
        } else {
            UnsafeInterpolation
        }
        val loggingLibraryContainer =
            LoggingLibraryContainer(IosLoggingLibraryFactory())

        val writerDispatcher = DelegatingLogDispatcher(
            Clock.System,
            logWriters = listOf(KermitLogWriter())
        )
        InnerLogger.init(
            writerDispatcher,
            interpolationConfiguration,
            loggingLibraryContainer.runMetadata
        )
    }
}
