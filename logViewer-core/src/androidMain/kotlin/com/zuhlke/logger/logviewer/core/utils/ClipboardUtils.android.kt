package com.zuhlke.logger.logviewer.core.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry

internal actual fun String.toClipEntry(): ClipEntry =
    ClipData.newPlainText("log message", this).toClipEntry()
