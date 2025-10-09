package com.zuhlke.logging

internal interface InterpolationConfiguration {

    fun interpolate(interpolatable: Interpolatable): String {
        val params = interpolatable.params.map(::interpolateParameter)
        return buildString {
            append(interpolatable.parts[0])
            for (i in params.indices) {
                append(params[i])
                append(interpolatable.parts[i + 1])
            }
        }
    }

    fun interpolateParameter(param: Any): String

    data object SafeInterpolation : InterpolationConfiguration {
        override fun interpolateParameter(param: Any): String = when (param) {
            is HashArgument -> param.arg.toString().hashCode().toString()
            is PublicArgument -> param.arg.toString()
            else -> "<redacted>"
        }
    }

    data object UnsafeInterpolation : InterpolationConfiguration {
        override fun interpolateParameter(param: Any): String = when (param) {
            is HashArgument -> param.arg.toString()
            is PublicArgument -> param.arg.toString()
            else -> param.toString()
        }
    }
}
