package com.zuhlke.logging.viewer.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
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

@OptIn(ExperimentalTime::class)
val AppRun.snapshot: AppRunSnapshot
    get() {
        return AppRunSnapshot(
            launchDate = launchDate,
            appVersion = appVersion,
            operatingSystemVersion = osVersion,
            device = device
        )
    }

@Serializable
@OptIn(ExperimentalTime::class)
data class AppRunSnapshot(
    @Contextual // TODO remove when AS Kotlin plugin is updated
    val launchDate: Instant,
    val appVersion: String,
    val operatingSystemVersion: String,
    val device: String
)