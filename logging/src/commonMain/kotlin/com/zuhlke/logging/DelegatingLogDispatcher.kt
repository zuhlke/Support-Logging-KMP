package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
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

@OptIn(ExperimentalTime::class)
// TODO: give better name
internal class DelegatingLogDispatcher(
    val clock: Clock,
    val logWriters: List<LogWriter>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
) : LogDispatcher {
    private val coroutineScope = CoroutineScope(
        dispatcher +
            SupervisorJob() +
            CoroutineName("ZuhkleLogger") +
            CoroutineExceptionHandler { _, throwable ->
                // can't log it, we're the logger
                println("DelegatingLogDispatcher: Uncaught exception in writer coroutine")
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
                    is Loggable.AppRun -> logWriters.forEach { writer ->
                        writer.writeAppRun(
                            launchDate = loggable.launchDate,
                            appVersion = loggable.appVersion,
                            osVersion = loggable.operatingSystemVersion,
                            device = loggable.device
                        )
                    }

                    is Loggable.LogRecord -> logWriters.forEach { writer ->
                        writer.writeLog(
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

    override fun init(runMetadata: RunMetadata) {
        loggingChannel.trySend(
            Loggable.AppRun(
                launchDate = clock.now(),
                appVersion = runMetadata.appVersion,
                operatingSystemVersion = runMetadata.osVersion,
                device = runMetadata.device
            )
        )
    }

    override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?) {
        loggingChannel.trySend(
            Loggable.LogRecord(
                timestamp = clock.now(),
                severity = severity,
                tag = tag,
                message = message,
                throwable = throwable
            )
        )
    }

    internal sealed class Loggable {
        internal data class AppRun(
            val launchDate: Instant,
            val appVersion: String,
            val operatingSystemVersion: String,
            val device: String
        ) : Loggable()

        internal data class LogRecord(
            val timestamp: Instant,
            val severity: Severity,
            val tag: String,
            val message: String,
            val throwable: Throwable?
        ) : Loggable()
    }
}
