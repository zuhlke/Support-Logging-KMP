package com.zuhlke.logger.logviewer.core.ui.utils

import com.zuhlke.logging.core.data.model.Severity
import org.jetbrains.compose.resources.StringResource
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.severity_assert
import supportloggingkmp.logviewer_core.generated.resources.severity_debug
import supportloggingkmp.logviewer_core.generated.resources.severity_error
import supportloggingkmp.logviewer_core.generated.resources.severity_info
import supportloggingkmp.logviewer_core.generated.resources.severity_verbose
import supportloggingkmp.logviewer_core.generated.resources.severity_warning

val Severity.stringResource: StringResource
    get() {
        return when (this) {
            Severity.Verbose -> Res.string.severity_verbose
            Severity.Debug -> Res.string.severity_debug
            Severity.Info -> Res.string.severity_info
            Severity.Warn -> Res.string.severity_warning
            Severity.Error -> Res.string.severity_error
            Severity.Assert -> Res.string.severity_assert
        }
    }
