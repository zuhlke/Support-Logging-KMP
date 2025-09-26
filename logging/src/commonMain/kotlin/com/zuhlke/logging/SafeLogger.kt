package com.zuhlke.logging

import io.exoquery.terpal.Interpolator
import io.exoquery.terpal.InterpolatorFunction
import io.exoquery.terpal.Messages

public class SafeLogger(private val tag: String) {

    public fun d(message: Interpolatable) {
        InnerLogger.log(Severity.Debug, tag, message, throwable = null)
    }

    public fun d(message: () -> Interpolatable) {
        InnerLogger.log(Severity.Debug, tag, message, throwable = null)
    }

    public fun e(throwable: Throwable?, message: Interpolatable) {
        InnerLogger.log(Severity.Error, tag, message, throwable = throwable)
    }

    public fun e(throwable: Throwable?, message: () -> Interpolatable) {
        InnerLogger.log(Severity.Error, tag, message, throwable = throwable)
    }
}

@InterpolatorFunction<SafeStringInterpolator>(SafeStringInterpolator::class)
public fun safeString(@Suppress("unused") string: String): Interpolatable =
    Messages.throwPluginNotExecuted()

public data class Interpolatable(
    internal val parts: List<String>,
    internal val params: List<Any>
)

internal data class PublicArgument(val arg: Any)
internal data class HashArgument(val arg: Any)

public fun public(value: Any): Any {
    return PublicArgument(value)
}

public fun hash(value: Any): Any {
    return HashArgument(value)
}

internal object SafeStringInterpolator : Interpolator<Any, Interpolatable> {
    override fun interpolate(parts: () -> List<String>, params: () -> List<Any>): Interpolatable {
        return Interpolatable(parts(), params())
    }
}