package com.zuhlke.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.SimpleFormatter
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import com.zuhlke.logging.data.LogDao
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalAtomicApi::class)
internal object InnerLogger {

    private val initialised = AtomicBoolean(false)
    private lateinit var outputConfiguration: OutputConfiguration
    private lateinit var interpolationConfiguration: InterpolationConfiguration

    fun init(
        outputConfiguration: OutputConfiguration,
        interpolationConfiguration: InterpolationConfiguration,
        runMetadata: RunMetadata
    ) {
        if (initialised.compareAndSet(expectedValue = false, newValue = true)) {
            this.outputConfiguration = outputConfiguration
            this.interpolationConfiguration = interpolationConfiguration
            outputConfiguration.logAppRun(
                appVersion = runMetadata.appVersion,
                operatingSystemVersion = runMetadata.operatingSystemVersion,
                device = runMetadata.device
            )
        } else {
            throw IllegalStateException("InnerLogger is already initialized")
        }
    }

    fun log(severity: Severity, tag: String, message: () -> Interpolatable, throwable: Throwable?) {
        if (!initialised.load()) {
            throw IllegalStateException("InnerLogger is not initialized")
        }
        log(severity, tag, message(), throwable)
    }

    fun log(severity: Severity, tag: String, message: Interpolatable, throwable: Throwable?) {
        if (!initialised.load()) {
            throw IllegalStateException("InnerLogger is not initialized")
        }
        val finalMessage = interpolationConfiguration.interpolate(message)
        outputConfiguration.log(severity, tag, finalMessage, throwable)
    }
}

// TODO: does it have to be sealed?
internal sealed interface OutputConfiguration {
    fun log(severity: Severity, tag: String, message: String, throwable: Throwable?)
    fun logAppRun(appVersion: String, operatingSystemVersion: String, device: String)

    data class DatabaseWriting(private val logDao: LogDao) : OutputConfiguration {

        val roomLogWriter = RoomLogWriter(logDao)
        val logger = Logger(
            loggerConfigInit(
                platformLogWriter(SimpleFormatter),
                roomLogWriter
            )
        )

        @OptIn(ExperimentalTime::class)
        override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?) {
            // TODO: do we have to do it through Kermit? Shall we call logDao.insert directly?
            logger.log(
                severity = severity.toKermitSeverity(),
                tag = tag,
                message = message,
                throwable = throwable
            )
        }

        // TODO: refactor
        override fun logAppRun(appVersion: String, operatingSystemVersion: String, device: String) {
            roomLogWriter.init(appVersion, operatingSystemVersion, device)
        }
    }

    object NoOp : OutputConfiguration {
        override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?) {
            // no-op
        }

        override fun logAppRun(appVersion: String, operatingSystemVersion: String, device: String) {
            // no-op
        }
    }
}

internal sealed interface InterpolationConfiguration {

    fun interpolate(interpolatable: Interpolatable): String {
        val params = interpolatable.params.map(::interpolate)
        return buildString {
            append(interpolatable.parts[0])
            for (i in params.indices) {
                append(params[i])
                append(interpolatable.parts[i + 1])
            }
        }
    }

    fun interpolate(param: Any): String

    data object SafeInterpolation : InterpolationConfiguration {
        override fun interpolate(param: Any): String = when (param) {
            is HashArgument -> param.arg.toString().hashCode().toString()
            is PublicArgument -> param.arg.toString()
            else -> "<redacted>"
        }
    }

    data object UnsafeInterpolation : InterpolationConfiguration {
        override fun interpolate(param: Any): String = when (param) {
            is HashArgument -> param.arg.toString()
            is PublicArgument -> param.arg.toString()
            else -> param.toString()
        }
    }
}
