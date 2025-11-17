package com.zuhlke.logging.utils.fakes

import com.zuhlke.logging.InnerLoggerInterface
import com.zuhlke.logging.Interpolatable
import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity

internal class FakeInnerLogger : InnerLoggerInterface {

    var runMetadata: RunMetadata? = null
        private set
    private val _logs = mutableListOf<FakeLogEntry>()
    val logs: List<FakeLogEntry> = _logs

    override fun logMetadata(runMetadata: RunMetadata) {
        this.runMetadata = runMetadata
    }

    override fun log(
        severity: Severity,
        tag: String,
        message: () -> Interpolatable,
        throwable: Throwable?
    ) {
        _logs.add(FakeLogEntry.LogWithLazyMessage(severity, tag, message, throwable))
    }

    override fun log(
        severity: Severity,
        tag: String,
        message: Interpolatable,
        throwable: Throwable?
    ) {
        _logs.add(FakeLogEntry.LogWithConcreteMessage(severity, tag, message, throwable))
    }
}

internal sealed class FakeLogEntry {

    data class LogWithConcreteMessage(
        val severity: Severity,
        val tag: String,
        val message: Interpolatable,
        val throwable: Throwable?
    ) : FakeLogEntry()

    data class LogWithLazyMessage(
        val severity: Severity,
        val tag: String,
        val message: () -> Interpolatable,
        val throwable: Throwable?
    ) : FakeLogEntry()
}