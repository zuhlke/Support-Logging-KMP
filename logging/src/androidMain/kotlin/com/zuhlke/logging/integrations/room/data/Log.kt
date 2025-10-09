package com.zuhlke.logging.integrations.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zuhlke.logging.data.Severity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity
@OptIn(ExperimentalTime::class)
internal data class Log(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Instant,
    val severity: Severity,
    val message: String,
    val tag: String,
    val throwable: String?,
    val appRunId: Int
)
