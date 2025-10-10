package com.zuhlke.logging.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.getMetadata
import com.zuhlke.logging.integrations.room.data.LogDatabase
import kotlinx.coroutines.Dispatchers

internal class AndroidLoggingLibraryFactory private constructor(context: Context) : LoggingLibraryFactory {

    val logRoomDatabase: LogDatabase by lazy { createLogRoomDatabase() }
    private val applicationContext = context.applicationContext

    private fun createLogRoomDatabase(): LogDatabase {
        val dbFile = applicationContext.getDatabasePath(LOG_DB_FILENAME)
        return Room.databaseBuilder<LogDatabase>(
            context = applicationContext,
            name = dbFile.absolutePath
        )
            .setDriver(AndroidSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override fun getMetadata(): RunMetadata = getMetadata(applicationContext)

    companion object {
        @Volatile
        private var instance: AndroidLoggingLibraryFactory? = null

        fun get(context: Context): AndroidLoggingLibraryFactory {
            return instance ?: synchronized(this) {
                instance ?: AndroidLoggingLibraryFactory(context).also { instance = it }
            }
        }
    }
}

internal const val LOG_DB_FILENAME = "logs.db"
