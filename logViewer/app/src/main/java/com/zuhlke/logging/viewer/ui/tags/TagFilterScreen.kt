package com.zuhlke.logging.viewer.ui.tags

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zuhlke.logging.viewer.R
import com.zuhlke.logging.viewer.ui.details.widgets.CheckboxRow
import com.zuhlke.logging.viewer.ui.theme.LogsViewerTheme

@Composable
fun TagFilterScreen(
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
                        painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.close)
                    )
                }
            },
            title = {
                Text(text = stringResource(R.string.filter_tags))
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
fun TagScreenPreview() {
    LogsViewerTheme {
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
