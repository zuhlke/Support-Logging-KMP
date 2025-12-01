package com.zuhlke.logger.logviewer.core.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry

actual fun String.toClipEntry(): ClipEntry {
    return ClipData.newPlainText("log message", this).toClipEntry()
}