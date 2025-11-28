package com.zuhlke.logging.viewer.ui.utils

import androidx.annotation.StringRes
import com.zuhlke.logging.core.data.model.Severity
import com.zuhlke.logging.viewer.R

@get:StringRes
val Severity.stringResource: Int
    get() {
        return when (this) {
            Severity.Verbose -> R.string.severity_verbose
            Severity.Debug -> R.string.severity_debug
            Severity.Info -> R.string.severity_info
            Severity.Warn -> R.string.severity_warning
            Severity.Error -> R.string.severity_error
            Severity.Assert -> R.string.severity_assert
        }
    }
