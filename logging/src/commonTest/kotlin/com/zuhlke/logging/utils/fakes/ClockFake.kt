package com.zuhlke.logging.utils.fakes

import kotlin.time.Clock
import kotlin.time.Instant

class ClockFake(private val now: Instant) : Clock {
    override fun now() = now
}
