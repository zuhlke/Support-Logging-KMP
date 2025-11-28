package com.zuhlke.logging.viewer.data.contentprovider

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetLogSharingApps @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context
) {
    operator fun invoke(): List<LogSharingApp> {
        val packages =
            applicationContext.packageManager.getInstalledPackages(
                PackageManager.GET_PROVIDERS or PackageManager.GET_META_DATA
            )
        val authorities = packages.flatMap { packageInfo ->
            val list = packageInfo.providers?.mapNotNull { contentProvider ->
                val logProvider =
                    contentProvider.metaData?.getString("com.zuhlke.logging.LOGS_PROVIDER")
                        ?: return@mapNotNull null
                val info = packageInfo.applicationInfo
                LogSharingApp(
                    name =
                    info?.let {
                        applicationContext.packageManager.getApplicationLabel(it).toString()
                    }
                        ?: packageInfo.packageName,
                    packageName = packageInfo.packageName,
                    authority = logProvider
                )
            }
            list ?: emptyList()
        }

        return authorities
    }
}

data class LogSharingApp(val name: String, val packageName: String, val authority: String)
