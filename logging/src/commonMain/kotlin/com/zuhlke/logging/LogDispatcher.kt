package com.zuhlke.logging

import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.data.Severity
import kotlin.time.Instant

internal interface LogDispatcher {
    fun init(runMetadata: RunMetadata)
    fun log(severity: Severity, tag: String, message: String, throwable: Throwable? = null)
}
