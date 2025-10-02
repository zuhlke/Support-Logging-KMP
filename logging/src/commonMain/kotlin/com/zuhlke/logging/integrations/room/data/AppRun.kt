package com.zuhlke.logging.integrations.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity
internal class AppRun(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val launchDate: Instant,
    val appVersion: String,
    val operatingSystemVersion: String,
    val device: String
)
