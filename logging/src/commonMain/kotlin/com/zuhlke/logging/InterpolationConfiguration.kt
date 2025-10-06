package com.zuhlke.logging

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
