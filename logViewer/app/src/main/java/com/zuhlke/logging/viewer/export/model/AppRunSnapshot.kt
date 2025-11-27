package com.zuhlke.logging.viewer.export.model

import com.zuhlke.logging.viewer.data.AppRun
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class AppRunSnapshot(
    val launchDate: Instant,
    val appVersion: String,
    val operatingSystemVersion: String,
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
