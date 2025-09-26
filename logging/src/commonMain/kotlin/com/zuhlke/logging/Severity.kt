package com.zuhlke.logging

public enum class Severity {
    Verbose,
    Debug,
    Info,
    Warn,
    Error,
    Assert
}

internal fun Severity.toKermitSeverity(): co.touchlab.kermit.Severity = when (this) {
    Severity.Verbose -> co.touchlab.kermit.Severity.Verbose
    Severity.Debug -> co.touchlab.kermit.Severity.Debug
    Severity.Info -> co.touchlab.kermit.Severity.Info
    Severity.Warn -> co.touchlab.kermit.Severity.Warn
    Severity.Error -> co.touchlab.kermit.Severity.Error
    Severity.Assert -> co.touchlab.kermit.Severity.Assert
}