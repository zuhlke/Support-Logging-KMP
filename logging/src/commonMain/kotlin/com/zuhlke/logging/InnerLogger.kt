package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import com.zuhlke.logging.interpolation.InterpolationConfiguration
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
internal class InnerLogger(
    val logDispatcher: LogDispatcher,
    val interpolationConfiguration: InterpolationConfiguration
) {
    fun logMetadata(runMetadata: RunMetadata) {
        logDispatcher.init(runMetadata)
    }

    fun log(severity: Severity, tag: String, message: () -> Interpolatable, throwable: Throwable?) {
        log(severity, tag, message(), throwable)
    }

    fun log(severity: Severity, tag: String, message: Interpolatable, throwable: Throwable?) {
        val finalMessage = interpolationConfiguration.interpolate(message)
        logDispatcher.log(
            severity = severity,
            tag = tag,
            message = finalMessage,
            throwable = throwable
        )
    }

    companion object {
        private val instance = AtomicReference<InnerLogger?>(null)

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

        val shared: InnerLogger
            get() = instance.load()
                ?: throw IllegalStateException("InnerLogger is not initialized. Call init() first.")
    }
}
