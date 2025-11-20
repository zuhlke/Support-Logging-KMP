package com.zuhlke.logging.integrations.kermit

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.SimpleFormatter

internal actual fun platformLogWriter(subsystem: String): LogWriter = LogcatWriter(SimpleFormatter)
