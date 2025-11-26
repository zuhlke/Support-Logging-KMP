package com.zuhlke.logging.viewer.navigation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.zuhlke.logging.viewer.navigation.results.LocalResultEventBus
import com.zuhlke.logging.viewer.navigation.results.ResultEffect
import com.zuhlke.logging.viewer.navigation.results.ResultEventBus
import com.zuhlke.logging.viewer.ui.details.AppDetailsScreen
import com.zuhlke.logging.viewer.ui.details.AppDetailsViewModel
import com.zuhlke.logging.viewer.ui.details.SearchScreen
import com.zuhlke.logging.viewer.ui.details.SearchState
import com.zuhlke.logging.viewer.ui.list.AppListScreen
import com.zuhlke.logging.viewer.ui.tags.TagFilterScreen
import com.zuhlke.logging.viewer.ui.tags.TagViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Navigation(modifier: Modifier) {
    val backStack = rememberNavBackStack(RouteAppList)

    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }
    // Override the defaults so that there isn't a horizontal space between the panes.
    // See b/418201867
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    val resultBus = remember { ResultEventBus() }
    CompositionLocalProvider(LocalResultEventBus.provides(resultBus)) {
        NavDisplay(
            backStack = backStack,
            sceneStrategy = dialogStrategy then listDetailStrategy,
            modifier = modifier.semantics {
                // Allows to use testTag() for UiAutomator resource-id.
                testTagsAsResourceId = true
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is RouteAppList -> NavEntry(
                        key,
                        metadata = ListDetailSceneStrategy.listPane(
                            detailPlaceholder = {
                                Placeholder(
                                    modifier = Modifier,
                                    "Choose a run from the list"
                                )
                            }
                        )
                    ) {
                        Log.d("Navigation", "inside RouteAppList")
                        AppListScreen(onAppClick = { authority ->
                            backStack.add(RouteAppDetails(authority))
                        })
                    }

                    is RouteSearch -> NavEntry(
                        key,
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) {
                        Log.d("Navigation", "inside RouteSearch")
                        val viewModel =
                            hiltViewModel<AppDetailsViewModel, AppDetailsViewModel.Factory>(
                                creationCallback = { factory ->
                                    Log.d("Navigation", "AppDetailsViewModel creationCallback")
                                    factory.create(
                                        key.authority,
                                        defaultSearchState = key.searchState
                                    )
                                }
                            )
                        ResultEffect<Set<String>>(resultKey = "tags") { result ->
                            Log.d("Navigation", "ResultEffect result = $result")
                            viewModel.setTags(result)
                        }
                        SearchScreen(
                            viewModel,
                            onTagSelectorRequested = { tagsState ->
                                backStack.add(RouteTagFilter(tagsState))
                            },
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    is RouteAppDetails -> NavEntry(
                        key,
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) {
                        Log.d("Navigation", "inside RouteAppDetails")
                        val viewModel =
                            hiltViewModel<AppDetailsViewModel, AppDetailsViewModel.Factory>(
                                creationCallback = { factory ->
                                    factory.create(
                                        key.authority,
                                        defaultSearchState = SearchState()
                                    )
                                }
                            )
                        AppDetailsScreen(
                            viewModel,
                            onSearch = { searchState ->
                                backStack.add(RouteSearch(key.authority, searchState))
                            },
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    is RouteTagFilter -> NavEntry(
                        key,
                        metadata = DialogSceneStrategy.dialog(
                            DialogProperties(
                                usePlatformDefaultWidth = false,
                                decorFitsSystemWindows = false
                            )
                        )
                    ) {
                        Log.d("Navigation", "inside RouteAppDetails")
                        val viewModel = hiltViewModel<TagViewModel, TagViewModel.Factory>(
                            creationCallback = { factory ->
                                factory.create(key.tagFilterState)
                            }
                        )
                        TagFilterScreen(
                            viewModel,
                            onTagsSelectionChanged = {
                                resultBus.sendResult<Set<String>>(result = it, resultKey = "tags")
                            },
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    else -> error("Unknown route: $key")
                }
            }
        )
    }
}

@Composable
fun Placeholder(modifier: Modifier, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .clip(RoundedCornerShape(48.dp))
    ) {
        Text(text)
    }
}
