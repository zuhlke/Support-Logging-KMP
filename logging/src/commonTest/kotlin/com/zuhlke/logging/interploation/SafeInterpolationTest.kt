package com.zuhlke.logging.interploation

import com.zuhlke.logging.HashParameter
import com.zuhlke.logging.PublicParameter
import com.zuhlke.logging.interpolation.SafeInterpolation
import kotlin.test.Test
import kotlin.test.assertEquals

class SafeInterpolationTest {

    private val sampleData =
        SampleData(username = "username", password = "password", accountBalance = 1000)

    @Test
    fun `hash parameters interpolate to hashed values`() {
        val result1 = SafeInterpolation.interpolateParameter(
            HashParameter("user name")
        )
        val result2 = SafeInterpolation.interpolateParameter(
            HashParameter(1337)
        )
        val result3 = SafeInterpolation.interpolateParameter(
            HashParameter(sampleData)
        )

        assertEquals("10c225c0", result1)
        assertEquals("00170c06", result2)
        assertEquals("50c72950", result3)
    }

    @Test
    fun `public parameters interpolate to original values`() {
        val result1 = SafeInterpolation.interpolateParameter(
            PublicParameter("screen name")
        )
        val result2 = SafeInterpolation.interpolateParameter(
            PublicParameter(1337)
        )
        val result3 = SafeInterpolation.interpolateParameter(
            PublicParameter(sampleData)
        )

        assertEquals("screen name", result1)
        assertEquals("1337", result2)
        assertEquals(
            "SampleData(username=username, password=password, accountBalance=1000)",
            result3
        )
    }

    @Test
    fun `non annotated parameters interpolate as redacted`() {
        val result1 = SafeInterpolation.interpolateParameter(
            "password"
        )
        val result2 = SafeInterpolation.interpolateParameter(
            1337
        )
        val result3 = SafeInterpolation.interpolateParameter(
            sampleData
        )

        assertEquals("<redacted>", result1)
        assertEquals("<redacted>", result2)
        assertEquals("<redacted>", result3)
    }

}

