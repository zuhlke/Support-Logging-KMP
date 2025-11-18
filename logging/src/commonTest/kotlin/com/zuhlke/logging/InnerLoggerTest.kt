package com.zuhlke.logging

import com.zuhlke.logging.data.Severity
import com.zuhlke.logging.interpolation.SafeInterpolation
import com.zuhlke.logging.utils.fakes.LogDispatcherFake
import com.zuhlke.logging.utils.fixtures.runMetadataFixture
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InnerLoggerTest {

    @BeforeTest
    fun setup() {
        InnerLogger.reset()
    }

    @Test
    fun `init calls logDispatcher`() {
        val subject = InnerLogger
        val logDispatcherFake = LogDispatcherFake()
        val interpolationConfiguration = SafeInterpolation

        subject.init(logDispatcherFake, interpolationConfiguration, runMetadataFixture)

        assertEquals(0, logDispatcherFake.logCalled)
        assertEquals(1, logDispatcherFake.initCalled)
        assertEquals(runMetadataFixture, logDispatcherFake.initCalls.first())
    }

    @Test
    fun `init cannot be called more than once`() {
        val subject = InnerLogger
        val logDispatcherFake = LogDispatcherFake()
        val interpolationConfiguration = SafeInterpolation

        subject.init(logDispatcherFake, interpolationConfiguration, runMetadataFixture)
        val exception = assertFailsWith<IllegalStateException> {
            subject.init(logDispatcherFake, interpolationConfiguration, runMetadataFixture)
        }

        assertEquals("InnerLogger is already initialized", exception.message)
        assertEquals(1, logDispatcherFake.initCalled)
        assertEquals(0, logDispatcherFake.logCalled)
    }

    @Test
    fun `log cannot be called before init`() {
        val exception = assertFailsWith<IllegalStateException> {
            InnerLogger.shared.log(
                severity = Severity.Error,
                tag = "TestTag",
                message = SafeString(parts = emptyList(), params = emptyList()),
                throwable = null
            )
        }

        assertEquals("InnerLogger is not initialized. Call init() first.", exception.message)
    }

    @Test
    fun `log calls logDispatcher`() {
        val logDispatcherFake = LogDispatcherFake()
        val interpolationConfiguration = SafeInterpolation
        val subject = InnerLogger(logDispatcherFake, interpolationConfiguration)

        subject.log(
            severity = Severity.Debug,
            tag = "TestTag",
            message = SafeString(parts = listOf("Test message"), params = emptyList()),
            throwable = null
        )
        val throwable = IllegalStateException("Test exception")
        subject.log(
            severity = Severity.Error,
            tag = "TestTag2",
            message = SafeString(parts = listOf("Test message2"), params = emptyList()),
            throwable = throwable
        )

        assertContentEquals(
            listOf(
                LogDispatcherFake.LogEntry(
                    severity = Severity.Debug,
                    tag = "TestTag",
                    message = "Test message",
                    throwable = null
                ),
                LogDispatcherFake.LogEntry(
                    severity = Severity.Error,
                    tag = "TestTag2",
                    message = "Test message2",
                    throwable = throwable
                )
            ),
            logDispatcherFake.logCalls
        )
    }

    // tests for interpolation
}
