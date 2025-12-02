package com.zuhlke.logging.integration.room

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.zuhlke.logging.integration.room.data.LOG_DB_FILENAME
import com.zuhlke.logging.integration.room.data.LogDatabase
import kotlinx.coroutines.Dispatchers

internal fun createLogRoomDatabase(applicationContext: Context): LogDatabase {
    val dbFile = applicationContext.getDatabasePath(LOG_DB_FILENAME)
    return Room.databaseBuilder<LogDatabase>(
        context = applicationContext,
        name = dbFile.absolutePath
    )
        .setDriver(AndroidSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
