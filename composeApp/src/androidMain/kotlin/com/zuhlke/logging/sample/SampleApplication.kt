package com.zuhlke.logging.sample

import android.app.Application
import com.zuhlke.logging.ZuhlkeLogger

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ZuhlkeLogger.initialize(this, useSafeInterpolation = true)
    }
}
