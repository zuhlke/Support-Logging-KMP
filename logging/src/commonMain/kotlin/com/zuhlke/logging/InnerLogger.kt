package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
internal object InnerLogger {

    private data class State(
        val logDispatcher: LogDispatcher,
        val interpolationConfiguration: InterpolationConfiguration
    )

    private val state = AtomicReference<State?>(null)

    fun init(
        logDispatcher: LogDispatcher,
        interpolationConfiguration: InterpolationConfiguration,
        runMetadata: RunMetadata
    ) {
        val newState = State(
            logDispatcher = logDispatcher,
            interpolationConfiguration = interpolationConfiguration
        )
        if (state.compareAndSet(expectedValue = null, newValue = newState)) {
            newState.logDispatcher.init(runMetadata)
        } else {
            throw IllegalStateException("GlobalLogger is already initialized")
        }
    }

    fun log(severity: Severity, tag: String, message: () -> Interpolatable, throwable: Throwable?) {
        if (state.load() == null) {
            throw IllegalStateException("GlobalLogger is not initialized")
        }
        log(severity, tag, message(), throwable)
    }

    fun log(severity: Severity, tag: String, message: Interpolatable, throwable: Throwable?) {
        val currentState = state.load()
        if (currentState == null) {
            throw IllegalStateException("GlobalLogger is not initialized")
        }
        val finalMessage = currentState.interpolationConfiguration.interpolate(message)
        currentState.logDispatcher.log(
            severity = severity,
            tag = tag,
            message = finalMessage,
            throwable = throwable
        )
    }
}
