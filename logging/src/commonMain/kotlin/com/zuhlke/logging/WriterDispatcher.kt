package com.zuhlke.logging

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal data class LoggerConfiguration(
    internal val logWriters: List<LogWriter>
)

@OptIn(ExperimentalTime::class)
// TODO: give better name
internal class WriterDispatcher(val clock: Clock, val configuration: LoggerConfiguration) {
    private val coroutineScope = CoroutineScope(
        Dispatchers.IO.limitedParallelism(1) +
                SupervisorJob() +
                CoroutineName("ZuhkleLogger") +
                CoroutineExceptionHandler { _, throwable ->
                    // can't log it, we're the logger -- print to standard error
                    println("RoomLogWriter: Uncaught exception in writer coroutine")
                    throwable.printStackTrace()
                }
    )

    private val loggingChannel: Channel<Loggable> = Channel(capacity = Int.MAX_VALUE)

    init {
        coroutineScope.launch {
            writer()
        }
    }

    private suspend fun writer() {
        while (currentCoroutineContext().isActive) {
            val result = loggingChannel.receiveCatching()

            result.getOrNull()?.let { loggable ->
                when (loggable) {
                    is Loggable.AppRun -> configuration.logWriters.forEach { writer ->
                        writer.logAppRun(
                            launchDate = loggable.launchDate,
                            appVersion = loggable.appVersion,
                            operatingSystemVersion = loggable.operatingSystemVersion,
                            device = loggable.device
                        )
                    }

                    is Loggable.LogRecord -> {
                        configuration.logWriters.forEach { writer ->
                            writer.log(
                                timestamp = loggable.timestamp,
                                severity = loggable.severity,
                                message = loggable.message,
                                tag = loggable.tag,
                                throwable = loggable.throwable
                            )
                        }
                    }
                }
            }
        }
    }

    fun init(runMetadata: RunMetadata) {
        loggingChannel.trySend(
            Loggable.AppRun(
                launchDate = clock.now(),
                appVersion = runMetadata.appVersion,
                operatingSystemVersion = runMetadata.operatingSystemVersion,
                device = runMetadata.device
            )
        )
    }

    fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        loggingChannel.trySend(
            Loggable.LogRecord(
                timestamp = clock.now(),
                severity = severity,
                message = message,
                tag = tag,
                throwable = throwable
            )
        )
    }
}

internal sealed class Loggable() {
    internal data class AppRun(
        val launchDate: Instant,
        val appVersion: String,
        val operatingSystemVersion: String,
        val device: String
    ) :
        Loggable()

    internal data class LogRecord(
        val timestamp: Instant,
        val severity: Severity,
        val message: String,
        val tag: String,
        val throwable: Throwable?
    ) : Loggable()
}

