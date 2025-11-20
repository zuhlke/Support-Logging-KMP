package com.zuhlke.logging

import android.app.Application
import android.content.pm.ApplicationInfo
import com.zuhlke.logging.di.AndroidLoggingLibraryFactory
import com.zuhlke.logging.integrations.kermit.KermitLogWriter
import com.zuhlke.logging.integrations.room.RoomLogWriter
import com.zuhlke.logging.interpolation.SafeInterpolation
import com.zuhlke.logging.interpolation.UnsafeInterpolation
import kotlin.time.Clock

public actual object ZuhlkeLogger {

    /**
     * Initializes the Zuhlke logging library for Android.
     *
     * @param application The Android application instance.
     * @param useSafeInterpolation If true, uses safe interpolation to avoid logging sensitive data.
     *                             Defaults to true in release builds and false in debug builds.
     */
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
        val factory = AndroidLoggingLibraryFactory.get(application)
        val logDao = factory.logRoomDatabase.logDao()

        val writerDispatcher = DelegatingLogDispatcher(
            Clock.System,
            logWriters = listOf(KermitLogWriter(subsystem = application.packageName), RoomLogWriter(logDao))
        )
        InnerLogger.init(
            writerDispatcher,
            interpolationConfiguration,
            factory.getMetadata()
        )
    }
}
