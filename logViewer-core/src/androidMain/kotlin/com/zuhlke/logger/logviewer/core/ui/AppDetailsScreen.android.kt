package com.zuhlke.logger.logviewer.core.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.zuhlke.logger.logviewer.core.export.AndroidShareService
import com.zuhlke.logger.logviewer.core.export.JsonLogExporter
import com.zuhlke.logger.logviewer.core.export.KotlinSerializationJsonLogConverter
import com.zuhlke.logger.logviewer.core.export.LogExporter

@Composable
internal actual fun platformLogExporter(): LogExporter {
    Log.d("Profiling", "platformLogExporter called")
    return JsonLogExporter(
        converter = KotlinSerializationJsonLogConverter(),
        shareService = AndroidShareService(LocalContext.current)
    )
}
