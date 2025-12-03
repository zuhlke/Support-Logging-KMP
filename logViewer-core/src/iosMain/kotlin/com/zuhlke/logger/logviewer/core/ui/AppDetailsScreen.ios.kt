package com.zuhlke.logger.logviewer.core.ui

import androidx.compose.runtime.Composable
import com.zuhlke.logger.logviewer.core.export.IosShareService
import com.zuhlke.logger.logviewer.core.export.JsonLogExporter
import com.zuhlke.logger.logviewer.core.export.KotlinSerializationJsonLogConverter
import com.zuhlke.logger.logviewer.core.export.LogExporter

@Composable
actual fun platformLogExporter(): LogExporter = JsonLogExporter(
    converter = KotlinSerializationJsonLogConverter(),
    shareService = IosShareService()
)
