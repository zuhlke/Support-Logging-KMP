package com.zuhlke.logging.interpolation

import com.zuhlke.logging.SafeString

internal interface InterpolationConfiguration {

    fun interpolate(safeString: SafeString): String {
        val params = safeString.params.map(::interpolateParameter)
        return buildString {
            if (safeString.parts.isNotEmpty()) {
                append(safeString.parts[0])
            }
            for (i in params.indices) {
                append(params[i])
                append(safeString.parts[i + 1])
            }
        }
    }

    fun interpolateParameter(param: Any): String
}
