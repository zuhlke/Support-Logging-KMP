package com.zuhlke.logging.interpolation

import com.zuhlke.logging.HashParameter
import com.zuhlke.logging.PublicParameter

internal data object UnsafeInterpolation : InterpolationConfiguration {
    override fun interpolateParameter(param: Any): String = when (param) {
        is HashParameter -> param.arg.toString()
        is PublicParameter -> param.arg.toString()
        else -> param.toString()
    }
}
