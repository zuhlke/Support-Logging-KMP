package com.zuhlke.logging

import android.app.Application
import android.content.pm.ApplicationInfo
import com.zuhlke.logging.InterpolationConfiguration.SafeInterpolation
import com.zuhlke.logging.InterpolationConfiguration.UnsafeInterpolation
import com.zuhlke.logging.di.AndroidLoggingLibraryFactory
import com.zuhlke.logging.di.LoggingLibraryContainer
import com.zuhlke.logging.integrations.kermit.KermitLogWriter
import com.zuhlke.logging.integrations.room.RoomLogWriter
import kotlin.time.Clock

public actual object ZuhlkeLogger {

    public fun initialize(
        application: Application,
        useSafeInterpolation: Boolean = application.applicationInfo.flags and
                ApplicationInfo.FLAG_DEBUGGABLE == 0
    ) {
        val interpolationConfiguration = if (useSafeInterpolation) {
            SafeInterpolation
        } else {
            UnsafeInterpolation
        }
        val factory = AndroidLoggingLibraryFactory(application)
        val loggingLibraryContainer = LoggingLibraryContainer(factory)
        val logDao = factory.createLogRoomDatabase().logDao()

        SharedLogDaoHolder.logDao = logDao
        val writerDispatcher = DelegatingLogDispatcher(
            Clock.System,
            logWriters = listOf(KermitLogWriter(), RoomLogWriter(logDao))
        )
        InnerLogger.init(
            writerDispatcher,
            interpolationConfiguration,
            loggingLibraryContainer.runMetadata
        )
    }
}
