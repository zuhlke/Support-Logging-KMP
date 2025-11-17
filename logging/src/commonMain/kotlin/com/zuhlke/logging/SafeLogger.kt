package com.zuhlke.logging

import com.zuhlke.logging.data.Severity
import io.exoquery.terpal.Interpolator
import io.exoquery.terpal.InterpolatorFunction
import io.exoquery.terpal.Messages

public class SafeLogger(private val tag: String) {

    public fun v(message: Interpolatable) {
        InnerLogger.shared.log(Severity.Verbose, tag, message, throwable = null)
    }

    public fun v(message: () -> Interpolatable) {
        InnerLogger.shared.log(Severity.Verbose, tag, message, throwable = null)
    }

    public fun d(message: Interpolatable) {
        InnerLogger.shared.log(Severity.Debug, tag, message, throwable = null)
    }

    public fun d(message: () -> Interpolatable) {
        InnerLogger.shared.log(Severity.Debug, tag, message, throwable = null)
    }

    public fun i(message: Interpolatable) {
        InnerLogger.shared.log(Severity.Info, tag, message, throwable = null)
    }

    public fun i(message: () -> Interpolatable) {
        InnerLogger.shared.log(Severity.Info, tag, message, throwable = null)
    }

    public fun w(message: Interpolatable) {
        InnerLogger.shared.log(Severity.Warn, tag, message, throwable = null)
    }

    public fun w(message: () -> Interpolatable) {
        InnerLogger.shared.log(Severity.Warn, tag, message, throwable = null)
    }

    public fun e(throwable: Throwable?, message: Interpolatable) {
        InnerLogger.shared.log(Severity.Error, tag, message, throwable = throwable)
    }

    public fun e(throwable: Throwable?, message: () -> Interpolatable) {
        InnerLogger.shared.log(Severity.Error, tag, message, throwable = throwable)
    }

    public fun a(throwable: Throwable?, message: Interpolatable) {
        InnerLogger.shared.log(Severity.Assert, tag, message, throwable = throwable)
    }

    public fun a(throwable: Throwable?, message: () -> Interpolatable) {
        InnerLogger.shared.log(Severity.Assert, tag, message, throwable = throwable)
    }
}

@InterpolatorFunction<SafeStringInterpolator>(SafeStringInterpolator::class)
public fun safeString(@Suppress("unused") string: String): Interpolatable =
    Messages.throwPluginNotExecuted()

public class Interpolatable internal constructor(
    internal val parts: List<String>,
    internal val params: List<Any>
)

internal data class PublicParameter(val arg: Any)
internal data class HashParameter(val arg: Any)

// We return Any here to hide internal types from the public API
public fun public(value: Any): Any = PublicParameter(value)
public fun hash(value: Any): Any = HashParameter(value)

internal object SafeStringInterpolator : Interpolator<Any, Interpolatable> {
    override fun interpolate(parts: () -> List<String>, params: () -> List<Any>): Interpolatable =
        Interpolatable(parts(), params())
}
