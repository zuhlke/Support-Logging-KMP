package com.zuhlke.logger.logviewer.core.export

internal class IosShareService : ShareService {
    override suspend fun prepareToShare(json: String): ShareableFile {
        // TODO: iOS specific implementation to share the JSON string
        return object : ShareableFile {
            override val uriString: UriString
                get() = "ios-shareable-uri" // Placeholder URI string
        }
    }
}
