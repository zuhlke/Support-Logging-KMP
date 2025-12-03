package com.zuhlke.logger.logviewer.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.zuhlke.logger.logviewer.core.export.ShareableFile
import com.zuhlke.logger.logviewer.core.export.startShare
import kotlinx.coroutines.flow.SharedFlow

@Composable
internal actual fun ShareFileOnExportReady(exportReady: SharedFlow<ShareableFile>) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        exportReady.collect { shareableFile ->
            context.startShare(shareableFile.uriString.toUri())
        }
    }
}
