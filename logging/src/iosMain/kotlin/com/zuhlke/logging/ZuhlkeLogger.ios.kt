package com.zuhlke.logging

import com.zuhlke.logging.InterpolationConfiguration.SafeInterpolation
import com.zuhlke.logging.InterpolationConfiguration.UnsafeInterpolation
import com.zuhlke.logging.di.IosLoggingLibraryFactory
import com.zuhlke.logging.di.LoggingLibraryContainer

public actual object ZuhlkeLogger {
    public fun initialize(
        useSafeInterpolation: Boolean
    ) {
        val interpolationConfiguration = if (useSafeInterpolation) {
            SafeInterpolation
        } else {
            UnsafeInterpolation
        }
        val loggingLibraryContainer =
            LoggingLibraryContainer(IosLoggingLibraryFactory())
        InnerLogger.init(
            OutputConfiguration.DatabaseWriting(loggingLibraryContainer.logDao),
            interpolationConfiguration,
            loggingLibraryContainer.runMetadata
        )
    }
}
