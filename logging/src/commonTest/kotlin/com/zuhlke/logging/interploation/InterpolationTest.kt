package com.zuhlke.logging.interploation

import com.zuhlke.logging.SafeString
import com.zuhlke.logging.interpolation.InterpolationConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class InterpolationTest {

    @Test
    fun `interpolate returns empty string when no parts and no params`() {
        val subject: InterpolationConfiguration = object : InterpolationConfiguration {
            override fun interpolateParameter(param: Any): String = param.toString()
        }

        val result = subject.interpolate(SafeString(emptyList(), emptyList()))

        assertEquals("", result)
    }

    @Test
    fun `interpolate single part when one part and no params`() {
        val subject: InterpolationConfiguration = object : InterpolationConfiguration {
            override fun interpolateParameter(param: Any): String = param.toString()
        }

        val result = subject.interpolate(SafeString(listOf("sample"), emptyList()))

        assertEquals("sample", result)
    }

    @Test
    fun `interpolate assembles multiple parts and params in order`() {
        val subject: InterpolationConfiguration = object : InterpolationConfiguration {
            override fun interpolateParameter(param: Any): String = param.toString()
        }

        val result = subject.interpolate(
            SafeString(
                listOf("user name = ", ", user password = ", ""),
                listOf("sampleName", "samplePassword")
            )
        )

        assertEquals("user name = sampleName, user password = samplePassword", result)
    }

    @Test
    fun `interpolate delegates parameter formatting to interpolateParameter`() {
        val subject: InterpolationConfiguration = object : InterpolationConfiguration {
            override fun interpolateParameter(param: Any): String = param.hashCode().toHexString()
        }

        val result = subject.interpolate(
            SafeString(
                listOf("user name = ", ", user password = ", ""),
                listOf("sampleName", "samplePassword")
            )
        )

        assertEquals("user name = 08857f55, user password = 1b584a65", result)
    }
}
