package com.zuhlke.logging

import com.zuhlke.logging.data.Severity

/**
 * The default Logger API
 *
 * @constructor Creates a new SafeLogger instance with the given tag
 */
public class SafeLogger(private val tag: String) {

    /**
     * Log a message with Verbose severity using a [SafeString] message. This is the lowest severity level.
     *
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun v(message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(Severity.Verbose, tag, message, throwable)
    }

    /**
     * Log a message with Verbose severity. This is the lowest severity level.
     *
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun v(throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(Severity.Verbose, tag, message, throwable)
    }

    /**
     * Log a message with Debug severity using a [SafeString] message. This is above 'Verbose'.
     *
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun d(message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(Severity.Debug, tag, message, throwable)
    }

    /**
     * Log a message with Debug severity. This is above 'Verbose'.
     *
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun d(throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(Severity.Debug, tag, message, throwable)
    }

    /**
     * Log a message with Info severity using a [SafeString] message. This is above 'Debug'.
     *
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun i(message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(Severity.Info, tag, message, throwable)
    }

    /**
     * Log a message with Info severity. This is above 'Debug'.
     *
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun i(throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(Severity.Info, tag, message, throwable)
    }

    /**
     * Log a message with Warning severity using a [SafeString] message. This is above 'Info'.
     *
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun w(message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(Severity.Warn, tag, message, throwable)
    }

    /**
     * Log a message with Warning severity. This is above 'Info'.
     *
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun w(throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(Severity.Warn, tag, message, throwable)
    }

    /**
     * Log a message with Error severity using a [SafeString] message. This is above 'Error'.
     *
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun e(message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(Severity.Error, tag, message, throwable = throwable)
    }

    /**
     * Log a message with Error severity. This is above 'Warning'.
     *
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun e(throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(Severity.Error, tag, message, throwable = throwable)
    }

    /**
     * Log a message with Assertion severity using a [SafeString] message. This is the highest severity level.
     *
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun a(message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(Severity.Assert, tag, message, throwable = throwable)
    }

    /**
     * Log a message with Assertion severity. This is the highest severity level.
     *
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun a(throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(Severity.Assert, tag, message, throwable = throwable)
    }

    /**
     * Log a message with the specified severity using a [SafeString] message.
     *
     * @param severity - The severity level of the log.
     * @param message - The message to log.
     * @param throwable - Optional throwable to log.
     */
    public fun log(severity: Severity, message: SafeString, throwable: Throwable? = null) {
        InnerLogger.shared.log(severity, tag, message, throwable)
    }

    /**
     * Log a message with the specified severity.
     *
     * @param severity - The severity level of the log.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    public fun log(severity: Severity, throwable: Throwable? = null, message: () -> SafeString) {
        InnerLogger.shared.log(severity, tag, message, throwable)
    }
}
