package com.zuhlke.logger.logviewer.core.ui.tags

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zuhlke.logger.logviewer.core.ui.TagFilterState
import com.zuhlke.logger.logviewer.core.ui.theme.LogViewerTheme
import com.zuhlke.logger.logviewer.core.ui.widgets.CheckboxRow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.close
import supportloggingkmp.logviewer_core.generated.resources.filter_tags
import supportloggingkmp.logviewer_core.generated.resources.ic_close

@Composable
public fun TagFilterScreen(
    tagFilterState: TagFilterState,
    onTagsSelectionChanged: (Set<String>) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = TagViewModel.get(tagFilterState)
    TagFilterScreen(
        viewModel = viewModel,
        onTagsSelectionChanged = onTagsSelectionChanged,
        onBack = onBack
    )
}

@Composable
internal fun TagFilterScreen(
    viewModel: TagViewModel,
    onTagsSelectionChanged: (Set<String>) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.tags) {
        onTagsSelectionChanged(uiState.selectedTags)
    }

    TagFilterScreenContent(
        onBack = onBack,
        tags = uiState.tags,
        onTagClick = viewModel::onTagClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagFilterScreenContent(
    onBack: () -> Unit,
    tags: List<TagViewModel.TagUiState>,
    onTagClick: (String) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        painterResource(Res.drawable.ic_close),
                        contentDescription = stringResource(Res.string.close)
                    )
                }
            },
            title = {
                Text(text = stringResource(Res.string.filter_tags))
            }
        )
    }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(tags, key = { it.name }) { tag ->
                CheckboxRow(text = tag.name, checked = tag.isSelected, onCheckedChange = {
                    onTagClick(tag.name)
                })
            }
        }
    }
}

@Preview
@Composable
private fun TagScreenPreview() {
    LogViewerTheme {
        TagFilterScreenContent(
            onBack = {},
            tags = listOf(
                TagViewModel.TagUiState("HomeScreen", false),
                TagViewModel.TagUiState("Screen1", isSelected = true),
                TagViewModel.TagUiState("Screen2", false),
                TagViewModel.TagUiState("Upload", isSelected = true)
            ),
            onTagClick = {}
        )
    }
}
