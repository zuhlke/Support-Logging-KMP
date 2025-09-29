package com.zuhlke.logging.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zuhlke.logging.RunMetadata
import com.zuhlke.logging.data.LogDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal class IosLoggingLibraryFactory : LoggingLibraryFactory {

    override fun createLogRoomDatabase(): LogDatabase {
        val dbFile = "${fileDirectory()}/$LOG_DB_FILENAME"
        return Room.databaseBuilder<LogDatabase>(
            name = dbFile
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun fileDirectory(): String {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory).path!!
    }

    override fun getMetadata(): RunMetadata = getMetadataInternal()
}

internal fun getMetadataInternal(): RunMetadata {
    /*
    TODO: convert the following Swift code to Kotlin

    let appRun = AppRun(
        appVersion: Bundle.main.infoDictionary!["CFBundleVersion"] as! String,
        operatingSystemVersion: ProcessInfo.processInfo.operatingSystemVersionString,
        launchDate: appLaunchDate,
        device: deviceModel()
    )

    private func deviceModel() -> String {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8, value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value)))
        }
        return identifier
    }
     */
    return RunMetadata(
        appVersion = "Not implemented",
        operatingSystemVersion = "Not implemented",
        device = "Not implemented"
    )
}
