package com.zuhlke.logging.integration.room.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(
    entities = [AppRun::class, Log::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(Converters::class)
internal abstract class LogDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<LogDatabase> {
    override fun initialize(): LogDatabase
}

internal const val LOG_DB_FILENAME = "logs.db"
