package com.zuhlke.logging.di

import com.zuhlke.logging.RunMetadata

internal class IosLoggingLibraryFactory : LoggingLibraryFactory {
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
