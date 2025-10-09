package com.zuhlke.logging.utils.fakes

import com.zuhlke.logging.LogDispatcher
import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity

internal class LogDispatcherFake : LogDispatcher {

    data class LogEntry(
        val severity: Severity,
        val tag: String,
        val message: String,
        val throwable: Throwable?
    )

    private val _initCalls = mutableListOf<RunMetadata>()
    val initCalls: List<RunMetadata> = _initCalls
    val initCalled: Int
        get() = _initCalls.size

    private val _logCalls = mutableListOf<LogEntry>()
    val logCalls: List<LogEntry> = _logCalls
    val logCalled: Int
        get() = _logCalls.size

    override fun init(runMetadata: RunMetadata) {
        _initCalls.add(runMetadata)
    }

    override fun log(
        severity: Severity, tag: String, message: String, throwable: Throwable?
    ) {
        _logCalls.add(LogEntry(severity, tag, message, throwable))
    }
}