package com.zuhlke.logging

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Clock

@OptIn(ExperimentalAtomicApi::class)
internal object GlobalLogger {

    private val initialised = AtomicBoolean(false)
    private lateinit var writerDispatcher: WriterDispatcher
    private lateinit var interpolationConfiguration: InterpolationConfiguration

    fun init(
        clock: Clock,
        interpolationConfiguration: InterpolationConfiguration,
        runMetadata: RunMetadata,
        logWriters: List<LogWriter>
    ) {
        if (initialised.compareAndSet(expectedValue = false, newValue = true)) {
            this.writerDispatcher = WriterDispatcher(
                clock,
                LoggerConfiguration(
                    logWriters = logWriters
                )
            )
            this.interpolationConfiguration = interpolationConfiguration
            writerDispatcher.init(runMetadata)
        } else {
            throw IllegalStateException("GlobalLogger is already initialized")
        }
    }

    fun log(severity: Severity, tag: String, message: () -> Interpolatable, throwable: Throwable?) {
        if (!initialised.load()) {
            throw IllegalStateException("GlobalLogger is not initialized")
        }
        log(severity, tag, message(), throwable)
    }

    fun log(severity: Severity, tag: String, message: Interpolatable, throwable: Throwable?) {
        if (!initialised.load()) {
            throw IllegalStateException("GlobalLogger is not initialized")
        }
        val finalMessage = interpolationConfiguration.interpolate(message)
        writerDispatcher.log(severity, tag, finalMessage, throwable)
    }
}
