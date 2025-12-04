package com.zuhlke.logger.logviewer.core.ui

import androidx.compose.runtime.Composable
import com.zuhlke.logger.logviewer.core.export.ShareableFile
import kotlinx.coroutines.flow.SharedFlow

@Composable
internal actual fun ShareFileOnExportReady(exportReady: SharedFlow<ShareableFile>) {
    // TODO: iOS share implementation to be added
}
