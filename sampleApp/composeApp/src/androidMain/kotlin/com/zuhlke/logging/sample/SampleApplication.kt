package com.zuhlke.logging.sample

import android.app.Application
import com.zuhlke.logging.ZuhlkeLogger
import com.zuhlke.logging.integration.room.AndroidRoomLogWriter

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ZuhlkeLogger.initialize(
            application = this,
            useSafeInterpolation = true,
            setUncaughtExceptionHandler = true,
            AndroidRoomLogWriter(this)
        )
    }
}
