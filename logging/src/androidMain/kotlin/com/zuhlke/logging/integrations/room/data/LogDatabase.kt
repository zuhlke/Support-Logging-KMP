package com.zuhlke.logging.integrations.room.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [AppRun::class, Log::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
internal abstract class LogDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}
