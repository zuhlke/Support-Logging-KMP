package com.zuhlke.logging.viewer.data

enum class Severity {
    Verbose,
    Debug,
    Info,
    Warn,
    Error,
    Assert
}

fun Severity.toIosCompatibleString(): String = when (this) {
    Severity.Verbose -> "verbose"
    Severity.Debug -> "debug"
    Severity.Info -> "info"
    Severity.Warn -> "notice"
    Severity.Error -> "error"
    Severity.Assert -> "fault"
}
