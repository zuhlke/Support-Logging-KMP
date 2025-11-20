package com.zuhlke.logging.integrations.kermit

import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.OSLogWriter

internal actual fun platformLogWriter(subsystem: String): LogWriter {
    return OSLogWriter(DefaultFormatter, subsystem = subsystem)
}