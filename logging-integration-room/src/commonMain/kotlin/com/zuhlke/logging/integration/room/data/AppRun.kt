package com.zuhlke.logging.integration.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Entity
internal class AppRun(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val launchDate: Instant,
    val appVersion: String,
    val operatingSystemVersion: String,
    val device: String
)
