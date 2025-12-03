package com.zuhlke.logger.logviewer.core.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zuhlke.logger.logviewer.core.ui.TagFilterState
import com.zuhlke.logger.logviewer.core.ui.tags.TagViewModel.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class TagViewModel(tagFilterState: TagFilterState) : ViewModel() {

    private val selectedTags = MutableStateFlow(tagFilterState.selectedTags)
    private val allTagsSorted = tagFilterState.allTags.sorted()
    val uiState: StateFlow<UiState> = selectedTags.map { currentSelectedTags ->
        toUiState(currentSelectedTags)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        toUiState(tagFilterState.selectedTags)
    )

    private fun toUiState(currentSelectedTags: Set<String>) = UiState(
        allTagsSorted.map { name ->
            TagUiState(name, isSelected = name in currentSelectedTags)
        }
    )

    fun onTagClick(tag: String) {
        viewModelScope.launch {
            selectedTags.update { selectedTags ->
                if (selectedTags.contains(tag)) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }
            }
        }
    }

    data class UiState(val tags: List<TagUiState>)

    data class TagUiState(val name: String, val isSelected: Boolean)

    companion object {
        private val KEY_TAG_FILTER_STATE = object : CreationExtras.Key<TagFilterState> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tagFilterState = this[KEY_TAG_FILTER_STATE] as TagFilterState
                TagViewModel(tagFilterState)
            }
        }

        fun createFactoryExtras(tagFilterState: TagFilterState) = MutableCreationExtras().apply {
            set(KEY_TAG_FILTER_STATE, tagFilterState)
        }
    }
}

internal val UiState.selectedTags: Set<String>
    get() = tags.filter { it.isSelected }.map { it.name }.toSet()

@Composable
internal fun TagViewModel.Companion.get(tagFilterState: TagFilterState): TagViewModel {
    val extras = remember(tagFilterState) {
        createFactoryExtras(
            tagFilterState = tagFilterState
        )
    }
    return viewModel(
        factory = Factory,
        extras = extras
    )
}
