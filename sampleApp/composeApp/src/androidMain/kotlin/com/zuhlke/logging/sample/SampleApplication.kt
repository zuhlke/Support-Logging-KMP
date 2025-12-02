package com.zuhlke.logging.sample

import android.app.Application
import com.zuhlke.logging.ZuhlkeLogger
import com.zuhlke.logging.core.repository.AppRunsWithLogsRepository
import com.zuhlke.logging.integration.room.AndroidRoomLogWriter

class SampleApplication : Application() {

    private lateinit var logWriter: AndroidRoomLogWriter

    override fun onCreate() {
        super.onCreate()
        logWriter = AndroidRoomLogWriter(this)
        ZuhlkeLogger.initialize(
            application = this,
            useSafeInterpolation = true,
            setUncaughtExceptionHandler = true,
            logWriter
        )
    }

    fun getLogRepository(): AppRunsWithLogsRepository = logWriter.repository
}
