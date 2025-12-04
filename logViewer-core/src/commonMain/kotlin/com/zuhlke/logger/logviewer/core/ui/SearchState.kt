package com.zuhlke.logger.logviewer.core.ui

import com.zuhlke.logging.core.data.model.Severity
import kotlinx.serialization.Serializable

@Serializable
public data class SearchState(
    val messageText: String = "",
    val severities: Set<Severity> = emptySet(),
    val tags: Set<String> = emptySet()
)
