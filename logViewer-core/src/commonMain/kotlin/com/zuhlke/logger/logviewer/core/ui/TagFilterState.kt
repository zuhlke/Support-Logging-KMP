package com.zuhlke.logger.logviewer.core.ui

import kotlinx.serialization.Serializable

@Serializable
data class TagFilterState(val selectedTags: Set<String>, val allTags: Set<String>)