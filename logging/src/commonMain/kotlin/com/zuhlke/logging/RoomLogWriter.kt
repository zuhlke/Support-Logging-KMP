package com.zuhlke.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.zuhlke.logging.data.AppRun
import com.zuhlke.logging.data.Log
import com.zuhlke.logging.data.LogDao
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.properties.Delegates
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal open class RoomLogWriter(
    private val clock: Clock,
    private val logDao: LogDao
) : LogWriter() {

    constructor(
        logDao: LogDao
    ) : this(Clock.System, logDao)

    private var appRunId by Delegates.notNull<Int>()
    private val coroutineScope = CoroutineScope(
        Dispatchers.IO.limitedParallelism(1) +
                SupervisorJob() +
                CoroutineName("RoomLogWriter") +
                CoroutineExceptionHandler { _, throwable ->
                    // can't log it, we're the logger -- print to standard error
                    println("RoomLogWriter: Uncaught exception in writer coroutine")
                    throwable.printStackTrace()
                },
    )

    private val loggingChannel: Channel<Log> = Channel(capacity = Int.MAX_VALUE)

    init {
        coroutineScope.launch {
            writer()
        }
    }

    fun init(appVersion: String, operatingSystemVersion: String, device: String) {
        coroutineScope.launch { // TODO: ensure sequential init and logging
            val appRun = AppRun(
                launchDate = clock.now(),
                appVersion = appVersion,
                operatingSystemVersion = operatingSystemVersion,
                device = device
            )
            val rowId = logDao.insert(appRun)
            appRunId = logDao.getAppRunIdByRowId(rowId)
            println("AppRunId: $appRunId")
        }
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        loggingChannel.trySendBlocking(
            Log(
                timestamp = clock.now(),
                severity = severity.toInternalSeverity(),
                message = message,
                tag = tag,
                throwable = throwable?.stackTraceToString(),
                appRunId = appRunId
            )
        )
    }

    private suspend fun writer() {
        while (currentCoroutineContext().isActive) {
            val result = loggingChannel.receiveCatching()

            result.getOrNull()?.let { logDao.insert(it) }
        }
    }
}

private fun Severity.toInternalSeverity(): com.zuhlke.logging.Severity = when (this) {
    Severity.Verbose -> com.zuhlke.logging.Severity.Debug
    Severity.Debug -> com.zuhlke.logging.Severity.Debug
    Severity.Info -> com.zuhlke.logging.Severity.Debug
    Severity.Warn -> com.zuhlke.logging.Severity.Error
    Severity.Error -> com.zuhlke.logging.Severity.Error
    Severity.Assert -> com.zuhlke.logging.Severity.Error
}
