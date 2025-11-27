package com.zuhlke.logging.viewer.ui.details

import com.zuhlke.logging.viewer.data.model.Severity
import kotlinx.serialization.Serializable

@Serializable
data class SearchState(
    val messageText: String = "",
    val severities: Set<Severity> = emptySet(),
    val tags: Set<String> = emptySet()
)
