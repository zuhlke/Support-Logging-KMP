package com.zuhlke.logging.playground

import co.touchlab.kermit.Logger
import co.touchlab.kermit.SimpleFormatter
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import com.zuhlke.logging.Severity
import io.exoquery.terpal.Interpolator
import io.exoquery.terpal.InterpolatorFunction
import io.exoquery.terpal.Messages
import kotlin.contracts.ExperimentalContracts

internal class SafeLoggerExperimental(tag: String, debug: Boolean = false) {

    val parameterInterpolator: ParamInterpolator = if (debug) {
        DebugInterpolator()
    } else {
        ProductionInterpolator()
    }

    val logger = Logger(loggerConfigInit(platformLogWriter(SimpleFormatter)), tag = tag)

    private fun interpolate(interpolatable: Interpolatable): String {
        val params = interpolatable.params.map(parameterInterpolator::interpolate)
        return buildString {
            append(interpolatable.parts[0])
            for (i in params.indices) {
                append(params[i])
                append(interpolatable.parts[i + 1])
            }
        }
    }

    private val internal = Internal()

    @OptIn(ExperimentalContracts::class)
    operator fun invoke(action: Internal.() -> Interpolatable) {
        val interpolatable = internal.action()
        val messageString = interpolate(interpolatable)
        when (interpolatable.severity) {
            Severity.Debug -> {
                logger.d(messageString)
            }

            Severity.Error -> {
                logger.e(messageString)
            }

            else -> throw IllegalStateException("Unsupported severity: ${interpolatable.severity}")
        }
    }
}

internal class Internal() {
    @InterpolatorFunction<DebugSafeStringInterpolator>(DebugSafeStringInterpolator::class)
    fun debug(message: String): Interpolatable = Messages.throwPluginNotExecuted()

    @InterpolatorFunction<ErrorSafeStringInterpolator>(ErrorSafeStringInterpolator::class)
    fun error(message: String): Interpolatable = Messages.throwPluginNotExecuted()

    fun public(value: Any): PublicArgument {
        return PublicArgument(value)
    }

    fun hash(value: Any): HashArgument {
        return HashArgument(value)
    }
}

internal interface ParamInterpolator {
    fun interpolate(param: Any): String
}

internal class ProductionInterpolator : ParamInterpolator {
    override fun interpolate(param: Any): String {
        return when (param) {
            is HashArgument -> param.arg.toString().hashCode().toString()
            is PublicArgument -> param.arg.toString()
            else -> "<redacted>"
        }
    }
}

internal class DebugInterpolator : ParamInterpolator {
    override fun interpolate(param: Any): String {
        return when (param) {
            is HashArgument -> param.arg.toString()
            is PublicArgument -> param.arg.toString()
            else -> param.toString()
        }
    }
}

internal data class Interpolatable(
    internal val parts: List<String>,
    internal val params: List<Any>,
    internal val severity: Severity
)

internal data class PublicArgument(val arg: Any)
internal data class HashArgument(val arg: Any)

internal abstract class SafeStringInterpolator(private val severity: Severity) :
    Interpolator<Any, Interpolatable> {
    override fun interpolate(parts: () -> List<String>, params: () -> List<Any>): Interpolatable {
        return Interpolatable(parts(), params(), severity)
    }
}

internal object DebugSafeStringInterpolator : SafeStringInterpolator(Severity.Debug)
internal object ErrorSafeStringInterpolator : SafeStringInterpolator(Severity.Error)

