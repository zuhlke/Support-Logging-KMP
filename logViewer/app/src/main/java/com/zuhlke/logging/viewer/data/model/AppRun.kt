package com.zuhlke.logging.viewer.data.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class AppRun(
    val id: Int,
    val launchDate: Instant,
    val appVersion: String,
    val osVersion: String,
    val device: String
)
