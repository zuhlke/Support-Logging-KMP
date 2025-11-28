package com.zuhlke.logging

import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.utils.fakes.FakeInnerLogger
import com.zuhlke.logging.utils.fakes.FakeLogEntry
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class SafeLoggerTest {

    companion object {
        const val TAG = "test tag"
        val throwable = IllegalStateException("Test exception")
        val message = safeString("test message ${public(1337)}")
        val lazyMessage = { message }
    }

    private val innerLogger = FakeInnerLogger()
    private val testSubject = SafeLogger(TAG)

    @BeforeTest
    fun setup() {
        InnerLogger.init(innerLogger)
    }

    @Test
    fun `v with lazy message calls InnerLogger`() {
        testSubject.v(throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(Severity.Verbose)
    }

    @Test
    fun `v with concrete message calls InnerLogger`() {
        testSubject.v(message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(Severity.Verbose)
    }

    @Test
    fun `d with lazy message calls InnerLogger`() {
        testSubject.d(throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(Severity.Debug)
    }

    @Test
    fun `d with concrete message calls InnerLogger`() {
        testSubject.d(message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(Severity.Debug)
    }

    @Test
    fun `i with lazy message calls InnerLogger`() {
        testSubject.i(throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(Severity.Info)
    }

    @Test
    fun `i with concrete message calls InnerLogger`() {
        testSubject.i(message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(Severity.Info)
    }

    @Test
    fun `w with lazy message calls InnerLogger`() {
        testSubject.w(throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(Severity.Warn)
    }

    @Test
    fun `w with concrete message calls InnerLogger`() {
        testSubject.w(message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(Severity.Warn)
    }

    @Test
    fun `e with lazy message calls InnerLogger`() {
        testSubject.e(throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(Severity.Error)
    }

    @Test
    fun `e with concrete message calls InnerLogger`() {
        testSubject.e(message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(Severity.Error)
    }

    @Test
    fun `a with lazy message calls InnerLogger`() {
        testSubject.a(throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(Severity.Assert)
    }

    @Test
    fun `a with concrete message calls InnerLogger`() {
        testSubject.a(message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(Severity.Assert)
    }

    @Test
    fun `logs with all severities and concrete messages are recorded`() {
        for (severity in Severity.entries) {
            verifyLogWithConcreteMessage(severity)
        }
    }

    private fun verifyLogWithConcreteMessage(severity: Severity) {
        innerLogger.clearLogs()

        testSubject.log(severity, message, throwable)

        assertInnerLoggerWasCalledWithConcreteMessage(severity)
    }

    @Test
    fun `logs with all severities and lazy messages are recorded`() {
        for (severity in Severity.entries) {
            verifyLogWithLazyMessage(severity)
        }
    }

    private fun verifyLogWithLazyMessage(severity: Severity) {
        innerLogger.clearLogs()

        testSubject.log(severity, throwable, lazyMessage)

        assertInnerLoggerWasCalledWithLazyMessage(severity)
    }

    private fun assertInnerLoggerWasCalledWithConcreteMessage(severity: Severity) {
        val expected =
            listOf(FakeLogEntry.LogWithConcreteMessage(severity, TAG, message, throwable))
        assertContentEquals(expected, innerLogger.logs)
    }

    private fun assertInnerLoggerWasCalledWithLazyMessage(severity: Severity) {
        val expected =
            listOf(FakeLogEntry.LogWithLazyMessage(severity, TAG, lazyMessage, throwable))
        assertContentEquals(expected, innerLogger.logs)
    }
}
