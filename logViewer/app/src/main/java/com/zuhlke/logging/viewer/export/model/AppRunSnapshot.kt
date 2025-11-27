package com.zuhlke.logging.viewer.export.model

import com.zuhlke.logging.viewer.data.model.AppRun
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
<<<<<<<< HEAD:logViewer/app/src/main/java/com/zuhlke/logging/viewer/export/model/AppRunSnapshot.kt
import kotlinx.serialization.Serializable
========
>>>>>>>> origin/main:logViewer/app/src/main/java/com/zuhlke/logging/viewer/data/AppRun.kt

@Serializable
@OptIn(ExperimentalTime::class)
data class AppRunSnapshot(
    val launchDate: Instant,
    val appVersion: String,
    val operatingSystemVersion: String,
    val device: String
)
<<<<<<<< HEAD:logViewer/app/src/main/java/com/zuhlke/logging/viewer/export/model/AppRunSnapshot.kt

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
========
>>>>>>>> origin/main:logViewer/app/src/main/java/com/zuhlke/logging/viewer/data/AppRun.kt
