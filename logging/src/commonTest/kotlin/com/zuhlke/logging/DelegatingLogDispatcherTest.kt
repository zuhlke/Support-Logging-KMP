package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.time.Clock
import kotlin.time.Instant

class DelegatingLogDispatcherTest {

    @Test
    fun `init function calls writers`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val logWriter1 = LogWriterFake()
        val logWriter2 = LogWriterFake()
        val logWriters = listOf(logWriter1, logWriter2)
        val clock = object : Clock {
            override fun now(): Instant = Instant.parse("2025-02-03T04:05:06Z")
        }
        val runMetadata = RunMetadata(
            appVersion = "1.0.0",
            osVersion = "16.0",
            device = "Test Device"
        )
        val dispatcher = DelegatingLogDispatcher(clock, logWriters, testDispatcher)

        dispatcher.init(runMetadata)
        testScheduler.advanceUntilIdle()

        val expected = listOf(
            LogWriterFake.RunMetadata(
                launchDate = Instant.parse("2025-02-03T04:05:06Z"),
                appVersion = "1.0.0",
                osVersion = "16.0",
                device = "Test Device"
            )
        )
        assertContentEquals(expected, logWriter1.writeAppRunCalls)
        assertContentEquals(emptyList(), logWriter1.writeLogCalls)
        assertContentEquals(expected, logWriter2.writeAppRunCalls)
        assertContentEquals(emptyList(), logWriter2.writeLogCalls)
    }

    @Test
    fun `log function calls writers`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val logWriter1 = LogWriterFake()
        val logWriter2 = LogWriterFake()
        val logWriters = listOf(logWriter1, logWriter2)
        val clock = object : Clock {
            override fun now(): Instant = Instant.parse("2025-02-03T04:05:06Z")
        }
        val dispatcher = DelegatingLogDispatcher(clock, logWriters, testDispatcher)

        dispatcher.log(
            severity = Severity.Info,
            message = "Test message",
            tag = "TestTag",
            throwable = null
        )
        testScheduler.advanceUntilIdle()

        val expected = listOf(
            LogWriterFake.Log(
                timestamp = Instant.parse("2025-02-03T04:05:06Z"),
                severity = Severity.Info,
                message = "Test message",
                tag = "TestTag",
                throwable = null
            )
        )
        assertContentEquals(expected, logWriter1.writeLogCalls)
        assertContentEquals(expected, logWriter2.writeLogCalls)
        assertContentEquals(emptyList(), logWriter1.writeAppRunCalls)
        assertContentEquals(emptyList(), logWriter2.writeAppRunCalls)
    }

    @Test
    fun `log function calls writers with multiple severities`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val logWriter = LogWriterFake()
        val clock = object : Clock {
            override fun now(): Instant = Instant.parse("2025-02-03T04:05:06Z")
        }
        val dispatcher = DelegatingLogDispatcher(clock, listOf(logWriter), testDispatcher)

        dispatcher.log(
            severity = Severity.Info,
            message = "Test message1",
            tag = "TestTag2",
            throwable = null
        )
        val throwable = IllegalStateException("Test exception")
        dispatcher.log(
            severity = Severity.Error,
            message = "Test message1",
            tag = "TestTag2",
            throwable = throwable
        )
        testScheduler.advanceUntilIdle()

        val expected = listOf(
            LogWriterFake.Log(
                timestamp = Instant.parse("2025-02-03T04:05:06Z"),
                severity = Severity.Info,
                message = "Test message1",
                tag = "TestTag2",
                throwable = null
            ),
            LogWriterFake.Log(
                timestamp = Instant.parse("2025-02-03T04:05:06Z"),
                severity = Severity.Error,
                message = "Test message1",
                tag = "TestTag2",
                throwable = throwable
            )
        )
        assertContentEquals(expected, logWriter.writeLogCalls)
    }

}

class LogWriterFake : LogWriter {

    data class RunMetadata(
        val launchDate: Instant,
        val appVersion: String,
        val osVersion: String,
        val device: String
    )

    data class Log(
        val timestamp: Instant,
        val severity: Severity,
        val message: String,
        val tag: String,
        val throwable: Throwable?
    )

    val writeAppRunCalls = mutableListOf<RunMetadata>()
    val writeLogCalls = mutableListOf<Log>()

    override suspend fun writeAppRun(
        launchDate: Instant,
        appVersion: String,
        osVersion: String,
        device: String
    ) {
        writeAppRunCalls.add(RunMetadata(launchDate, appVersion, osVersion, device))
    }

    override suspend fun writeLog(
        timestamp: Instant,
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        writeLogCalls.add(Log(timestamp, severity, message, tag, throwable))
    }
}