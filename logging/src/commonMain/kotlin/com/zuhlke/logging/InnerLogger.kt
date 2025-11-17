package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import com.zuhlke.logging.interpolation.InterpolationConfiguration
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

internal interface InnerLoggerInterface {
    fun logMetadata(runMetadata: RunMetadata)
    fun log(severity: Severity, tag: String, message: () -> Interpolatable, throwable: Throwable?)
    fun log(severity: Severity, tag: String, message: Interpolatable, throwable: Throwable?)
}

@OptIn(ExperimentalAtomicApi::class)
internal class InnerLogger(
    val logDispatcher: LogDispatcher,
    val interpolationConfiguration: InterpolationConfiguration
) : InnerLoggerInterface {
    override fun logMetadata(runMetadata: RunMetadata) {
        logDispatcher.init(runMetadata)
    }

    override fun log(
        severity: Severity,
        tag: String,
        message: () -> Interpolatable,
        throwable: Throwable?
    ) {
        log(severity, tag, message(), throwable)
    }

    override fun log(
        severity: Severity,
        tag: String,
        message: Interpolatable,
        throwable: Throwable?
    ) {
        val finalMessage = interpolationConfiguration.interpolate(message)
        logDispatcher.log(
            severity = severity,
            tag = tag,
            message = finalMessage,
            throwable = throwable
        )
    }

    companion object {
        private val instance = AtomicReference<InnerLoggerInterface?>(null)

        fun init(
            logDispatcher: LogDispatcher,
            interpolationConfiguration: InterpolationConfiguration,
            runMetadata: RunMetadata
        ) {
            val logger = InnerLogger(logDispatcher, interpolationConfiguration)
            if (instance.compareAndSet(expectedValue = null, newValue = logger)) {
                logger.logMetadata(runMetadata)
            } else {
                throw IllegalStateException("InnerLogger is already initialized")
            }
        }

        internal fun reset() {
            instance.store(null)
        }

        internal fun init(innerLogger: InnerLoggerInterface) {
            instance.store(innerLogger)
        }

        val shared: InnerLoggerInterface
            get() = instance.load()
                ?: throw IllegalStateException("InnerLogger is not initialized. Call init() first.")
    }
}
