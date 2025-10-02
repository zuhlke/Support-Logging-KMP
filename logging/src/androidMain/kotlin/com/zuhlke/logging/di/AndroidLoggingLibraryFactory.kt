package com.zuhlke.logging.di

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.zuhlke.logging.RunMetadata
import com.zuhlke.logging.integrations.room.data.LogDatabase
import com.zuhlke.logging.getMetadata
import kotlinx.coroutines.Dispatchers

internal class AndroidLoggingLibraryFactory(private val app: Application) : LoggingLibraryFactory {
    override fun createLogRoomDatabase(): LogDatabase {
        val dbFile = app.getDatabasePath(LOG_DB_FILENAME)
        return Room.databaseBuilder<LogDatabase>(
            context = app,
            name = dbFile.absolutePath
        )
            .setDriver(AndroidSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override fun getMetadata(): RunMetadata = getMetadata(app)
}
