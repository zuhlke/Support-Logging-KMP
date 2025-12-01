package com.zuhlke.logging.core.data.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Represents a single run of the application.
 *
 * @property id Unique identifier for the app run.
 * @property launchDate The date and time when the app was launched.
 * @property appVersion The version of the application.
 * @property osVersion The version of the operating system.
 * @property device The device on which the app is running.
 */
@OptIn(ExperimentalTime::class)
public data class AppRun(
    val id: Int,
    val launchDate: Instant,
    val appVersion: String,
    val osVersion: String,
    val device: String
)
