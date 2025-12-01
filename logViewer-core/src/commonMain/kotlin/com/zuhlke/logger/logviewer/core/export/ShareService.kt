package com.zuhlke.logger.logviewer.core.export

interface ShareService {
    suspend fun share(json: String)
}