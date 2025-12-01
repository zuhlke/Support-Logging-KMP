package com.zuhlke.logging.core.data.model

/**
 * Represents an application run along with its associated log entries.
 *
 * @property appRun The application run ([AppRun]) information.
 * @property logEntries The list of log entries ([LogEntry]) associated with the application run.
 */
public data class AppRunWithLogs(val appRun: AppRun, val logEntries: List<LogEntry>)
