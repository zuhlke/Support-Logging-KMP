package com.zuhlke.logging

import android.content.Context
import android.os.Build

internal fun getMetadata(appContext: Context): RunMetadata {
    fun getAppVersion(): String = try {
        val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        val versionName = packageInfo.versionName ?: "Unknown"
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toString()
        }
        "$versionName ($versionCode)"
    } catch (_: Exception) {
        "Unknown"
    }

    return RunMetadata(
        appVersion = getAppVersion(),
        operatingSystemVersion = Build.VERSION.SDK_INT.toString(),
        device = Build.MODEL
    )
}
