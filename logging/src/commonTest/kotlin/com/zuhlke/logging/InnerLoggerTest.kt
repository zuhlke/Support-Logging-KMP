package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GlobalLoggerTest {

    @Test
    fun `init calls logDispatcher`() {
        val subject = InnerLogger
        val logDispatcherFake = LogDispatcherFake()
        val interpolationConfiguration = InterpolationConfiguration.SafeInterpolation
        val runMetadata = RunMetadata(
            appVersion = "1.0.0",
            osVersion = "15.0",
            device = "Pixel 5"
        )

        subject.init(logDispatcherFake, interpolationConfiguration, runMetadata)

        assertEquals(0, logDispatcherFake.logCalled)
        assertEquals(1, logDispatcherFake.initCalled)
        assertEquals(runMetadata, logDispatcherFake.initCalls.first())
    }

    @Test
    fun `init cannot be called more than once`() {
        val subject = InnerLogger
        val logDispatcherFake = LogDispatcherFake()
        val interpolationConfiguration = InterpolationConfiguration.SafeInterpolation
        val runMetadata = RunMetadata(
            appVersion = "1.0.0",
            osVersion = "15.0",
            device = "Pixel 5"
        )

        subject.init(logDispatcherFake, interpolationConfiguration, runMetadata)
        val exception = assertFailsWith<IllegalStateException> {
            subject.init(logDispatcherFake, interpolationConfiguration, runMetadata)
        }

        assertEquals("GlobalLogger is already initialized", exception.message)
        assertEquals(1, logDispatcherFake.initCalled)
        assertEquals(0, logDispatcherFake.logCalled)
    }

    // log can't be called before init
    // check atomicity
    // calls init
    // calls log
}

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