package com.zuhlke.logger.logviewer.core.export

internal interface ShareService {
    suspend fun prepareToShare(json: String): ShareableFile
}

internal interface ShareableFile {
    val uriString: UriString
}
