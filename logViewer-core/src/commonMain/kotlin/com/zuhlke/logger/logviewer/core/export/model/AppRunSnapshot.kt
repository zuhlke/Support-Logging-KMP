package com.zuhlke.logger.logviewer.core.export.model

import com.zuhlke.logging.core.data.model.AppRun
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalTime::class)
internal data class AppRunSnapshot(
    val launchDate: Instant,
    val appVersion: String,
    val operatingSystemVersion: String,
    val device: String
)

@OptIn(ExperimentalTime::class)
internal val AppRun.snapshot: AppRunSnapshot
    get() {
        return AppRunSnapshot(
            launchDate = launchDate,
            appVersion = appVersion,
            operatingSystemVersion = osVersion,
            device = device
        )
    }
