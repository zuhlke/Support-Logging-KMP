package com.zuhlke.logging

import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.utils.fakes.ClockFake
import com.zuhlke.logging.utils.fakes.LogWriterFake
import com.zuhlke.logging.utils.fixtures.nowFixture
import com.zuhlke.logging.utils.fixtures.runMetadataFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.time.Instant
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class DelegatingLogDispatcherTest {

    @Test
    fun `init function calls writers`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val logWriter1 = LogWriterFake()
        val logWriter2 = LogWriterFake()
        val clock = ClockFake(nowFixture)
        val dispatcher =
            DelegatingLogDispatcher(clock, listOf(logWriter1, logWriter2), testDispatcher)

        dispatcher.init(runMetadataFixture)
        testScheduler.advanceUntilIdle()

        val expected = listOf(
            LogWriterFake.AppRunArgs(
                launchDate = nowFixture,
                appVersion = runMetadataFixture.appVersion,
                osVersion = runMetadataFixture.osVersion,
                device = runMetadataFixture.device
            )
        )
        assertContentEquals(expected, logWriter1.writeAppRunArgs)
        assertContentEquals(emptyList(), logWriter1.writeLogCalls)
        assertContentEquals(expected, logWriter2.writeAppRunArgs)
        assertContentEquals(emptyList(), logWriter2.writeLogCalls)
    }

    @Test
    fun `log function calls writers`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val logWriter1 = LogWriterFake()
        val logWriter2 = LogWriterFake()
        val clock = ClockFake(nowFixture)
        val dispatcher =
            DelegatingLogDispatcher(clock, listOf(logWriter1, logWriter2), testDispatcher)

        dispatcher.log(
            severity = Severity.Info,
            message = "Test message",
            tag = "TestTag",
            throwable = null
        )
        testScheduler.advanceUntilIdle()

        val expected = listOf(
            LogWriterFake.LogCallArgs(
                timestamp = Instant.parse("2025-02-03T04:05:06Z"),
                severity = Severity.Info,
                message = "Test message",
                tag = "TestTag",
                throwable = null
            )
        )
        assertContentEquals(expected, logWriter1.writeLogCalls)
        assertContentEquals(expected, logWriter2.writeLogCalls)
        assertContentEquals(emptyList(), logWriter1.writeAppRunArgs)
        assertContentEquals(emptyList(), logWriter2.writeAppRunArgs)
    }

    @Test
    fun `log function calls writers with multiple severities`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val logWriter = LogWriterFake()
        val clock = ClockFake(nowFixture)
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
            LogWriterFake.LogCallArgs(
                timestamp = nowFixture,
                severity = Severity.Info,
                message = "Test message1",
                tag = "TestTag2",
                throwable = null
            ),
            LogWriterFake.LogCallArgs(
                timestamp = nowFixture,
                severity = Severity.Error,
                message = "Test message1",
                tag = "TestTag2",
                throwable = throwable
            )
        )
        assertContentEquals(expected, logWriter.writeLogCalls)
    }
}
