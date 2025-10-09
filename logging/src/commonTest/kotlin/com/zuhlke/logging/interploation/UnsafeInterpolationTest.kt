package com.zuhlke.logging.interploation

import com.zuhlke.logging.HashParameter
import com.zuhlke.logging.PublicParameter
import com.zuhlke.logging.interpolation.UnsafeInterpolation
import kotlin.test.Test
import kotlin.test.assertEquals

class UnsafeInterpolationTest {

    private val sampleData =
        SampleData(username = "username", password = "password", accountBalance = 1000)

    @Test
    fun `hash parameters interpolate to original values`() {
        val result1 = UnsafeInterpolation.interpolateParameter(
            HashParameter("user name")
        )
        val result2 = UnsafeInterpolation.interpolateParameter(
            HashParameter(1337)
        )
        val result3 = UnsafeInterpolation.interpolateParameter(
            HashParameter(sampleData)
        )

        assertEquals("user name", result1)
        assertEquals("1337", result2)
        assertEquals(
            "SampleData(username=username, password=password, accountBalance=1000)",
            result3
        )
    }

    @Test
    fun `public parameters interpolate to original values`() {
        val result1 = UnsafeInterpolation.interpolateParameter(
            PublicParameter("screen name")
        )
        val result2 = UnsafeInterpolation.interpolateParameter(
            PublicParameter(1337)
        )
        val result3 = UnsafeInterpolation.interpolateParameter(
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
    fun `non annotated parameters interpolate to original values`() {
        val result1 = UnsafeInterpolation.interpolateParameter(
            "password"
        )
        val result2 = UnsafeInterpolation.interpolateParameter(
            1337
        )
        val result3 = UnsafeInterpolation.interpolateParameter(
            sampleData
        )

        assertEquals("password", result1)
        assertEquals("1337", result2)
        assertEquals(
            "SampleData(username=username, password=password, accountBalance=1000)",
            result3
        )
    }
}
