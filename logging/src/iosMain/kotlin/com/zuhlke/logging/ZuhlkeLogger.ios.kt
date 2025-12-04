package com.zuhlke.logging

import com.zuhlke.logging.core.LogWriter
import com.zuhlke.logging.di.IosLoggingLibraryFactory
import com.zuhlke.logging.integrations.kermit.KermitLogWriter
import com.zuhlke.logging.interpolation.SafeInterpolation
import com.zuhlke.logging.interpolation.UnsafeInterpolation
import kotlin.time.Clock

public actual object ZuhlkeLogger {
    /**
     * Initializes the Zuhlke logging library for iOS.
     * @param useSafeInterpolation If true, uses safe interpolation to avoid logging sensitive data.
     * @param subsystem The logging subsystem identifier.
     */
    public fun initialize(
        useSafeInterpolation: Boolean,
        subsystem: String,
        logWriters: List<LogWriter>
    ) {
        val logDispatcher = DelegatingLogDispatcher(
            Clock.System,
            logWriters = listOf(KermitLogWriter(subsystem = subsystem)) + logWriters
        )

        val interpolationConfiguration = if (useSafeInterpolation) {
            SafeInterpolation
        } else {
            UnsafeInterpolation
        }

        val factory = IosLoggingLibraryFactory()
        val runMetadata = factory.getMetadata()

        InnerLogger.init(
            logDispatcher,
            interpolationConfiguration,
            runMetadata
        )
    }
}
