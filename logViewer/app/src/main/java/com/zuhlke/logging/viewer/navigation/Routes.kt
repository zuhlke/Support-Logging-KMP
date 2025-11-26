package com.zuhlke.logging.viewer.navigation

import androidx.navigation3.runtime.NavKey
import com.zuhlke.logging.viewer.ui.details.SearchState
import com.zuhlke.logging.viewer.ui.tags.TagFilterState
import kotlinx.serialization.Serializable

@Serializable
internal object RouteAppList : NavKey

@Serializable
internal data class RouteAppDetails(val authority: String) : NavKey

@Serializable
internal data class RouteSearch(val authority: String, val searchState: SearchState) : NavKey

@Serializable
internal data class RouteTagFilter(val tagFilterState: TagFilterState) : NavKey
