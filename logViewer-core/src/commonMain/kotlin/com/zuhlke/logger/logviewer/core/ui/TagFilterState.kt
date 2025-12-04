package com.zuhlke.logger.logviewer.core.ui

import kotlinx.serialization.Serializable

@Serializable
public data class TagFilterState(val selectedTags: Set<String>, val allTags: Set<String>)
