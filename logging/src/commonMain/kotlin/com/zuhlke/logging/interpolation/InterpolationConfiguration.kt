package com.zuhlke.logging.interpolation

import com.zuhlke.logging.Interpolatable

internal interface InterpolationConfiguration {

    fun interpolate(interpolatable: Interpolatable): String {
        val params = interpolatable.params.map(::interpolateParameter)
        return buildString {
            if (interpolatable.parts.isNotEmpty()) {
                append(interpolatable.parts[0])
            }
            for (i in params.indices) {
                append(params[i])
                append(interpolatable.parts[i + 1])
            }
        }
    }

    fun interpolateParameter(param: Any): String
}
