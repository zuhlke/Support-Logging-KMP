package com.zuhlke.logger.logviewer.core.export

import android.content.Context
import androidx.core.content.FileProvider
import java.io.File
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AndroidShareService(private val applicationContext: Context) : ShareService {
    @OptIn(ExperimentalTime::class)
    override suspend fun share(json: String) {
        val exportFile = File(applicationContext.cacheDir, "exports").apply { mkdir() }
            .resolve("log-${Clock.System.now()}.json")
            .also { it.writeText(json) }
        val shareableUri = FileProvider.getUriForFile(
            /* context = */ applicationContext,
            /* authority = */ "com.zuhlke.logging.viewer.fileprovider",
            /* file = */ exportFile
        )
        // TODO: fix. Needs an activity context to start the share intent
        applicationContext.startShare(shareableUri)
    }
}
