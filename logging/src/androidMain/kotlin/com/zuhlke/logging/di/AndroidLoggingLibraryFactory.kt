package com.zuhlke.logging.di

import android.content.Context
import com.zuhlke.logging.data.RunMetadata
import com.zuhlke.logging.getMetadata

internal class AndroidLoggingLibraryFactory private constructor(context: Context) :
    LoggingLibraryFactory {

    private val applicationContext = context.applicationContext

    override fun getMetadata(): RunMetadata = getMetadata(applicationContext)

    companion object {
        @Volatile
        private var instance: AndroidLoggingLibraryFactory? = null

        fun get(context: Context): AndroidLoggingLibraryFactory = instance ?: synchronized(this) {
            instance ?: AndroidLoggingLibraryFactory(context).also { instance = it }
        }
    }
}

internal const val LOG_DB_FILENAME = "logs.db"
