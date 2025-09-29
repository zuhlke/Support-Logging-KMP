package com.zuhlke.logging

import android.app.Application
import android.content.pm.ApplicationInfo
import com.zuhlke.logging.InterpolationConfiguration.SafeInterpolation
import com.zuhlke.logging.InterpolationConfiguration.UnsafeInterpolation
import com.zuhlke.logging.di.AndroidLoggingLibraryFactory
import com.zuhlke.logging.di.LoggingLibraryContainer

public actual object ZuhlkeLogger {

    public fun initialize(
        application: Application,
        useSafeInterpolation: Boolean = application.applicationInfo.flags and
            ApplicationInfo.FLAG_DEBUGGABLE ==
            0
    ) {
        val interpolationConfiguration = if (useSafeInterpolation) {
            SafeInterpolation
        } else {
            UnsafeInterpolation
        }
        val loggingLibraryContainer =
            LoggingLibraryContainer(AndroidLoggingLibraryFactory(application))
        SharedLogDaoHolder.logDao = loggingLibraryContainer.logDao
        InnerLogger.init(
            OutputConfiguration.DatabaseWriting(loggingLibraryContainer.logDao),
            interpolationConfiguration,
            loggingLibraryContainer.runMetadata
        )
    }
}
