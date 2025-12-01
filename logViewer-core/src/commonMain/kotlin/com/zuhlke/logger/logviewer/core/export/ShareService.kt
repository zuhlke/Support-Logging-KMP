package com.zuhlke.logger.logviewer.core.export

interface ShareService {
    suspend fun prepareToShare(json: String): ShareableFile
}

interface ShareableFile {
    val uriString: UriString
}
