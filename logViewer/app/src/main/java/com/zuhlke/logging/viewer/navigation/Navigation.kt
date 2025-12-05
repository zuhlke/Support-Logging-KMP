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
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.zuhlke.logger.logviewer.core.ui.AppDetailsScreen
import com.zuhlke.logger.logviewer.core.ui.SearchScreen
import com.zuhlke.logger.logviewer.core.ui.TagFilterState
import com.zuhlke.logger.logviewer.core.ui.tags.TagFilterScreen
import com.zuhlke.logging.viewer.data.contentprovider.ContentProviderAppRunsWithLogsRepository
import com.zuhlke.logging.viewer.navigation.results.rememberResultStore
import com.zuhlke.logging.viewer.ui.list.AppListScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Navigation(
    modifier: Modifier,
    appRunsWithLogsRepositoryFactory: ContentProviderAppRunsWithLogsRepository.Factory
) {
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

    val resultStore = rememberResultStore()

    val retainedValuesStoreRegistry = retainRetainedValuesStoreRegistry()

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
        onBack = {
            val last = backStack.removeLastOrNull()
            if (last is RouteAppDetails) {
                retainedValuesStoreRegistry.clearChild("details")
            }
        },
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
                    val tags = resultStore.getResultState<Set<String>?>()
                    SearchScreen(
                        appRunsWithLogsRepositoryFactory.create(key.authority),
                        tags = tags ?: emptySet(),
                        onTagSelectorRequested = { tagsState: TagFilterState ->
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
                    retainedValuesStoreRegistry.LocalRetainedValuesStoreProvider("details") {
                        Log.d("Navigation", "inside RouteAppDetails")
                        AppDetailsScreen(
                            appRunsWithLogsRepositoryFactory.create(key.authority),
                            onSearch = { searchState ->
                                backStack.add(RouteSearch(key.authority, searchState))
                            },
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
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
                    TagFilterScreen(
                        key.tagFilterState,
                        onTagsSelectionChanged = {
                            resultStore.setResult<Set<String>>(result = it)
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
