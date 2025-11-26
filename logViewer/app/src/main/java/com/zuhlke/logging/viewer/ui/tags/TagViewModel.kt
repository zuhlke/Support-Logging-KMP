package com.zuhlke.logging.viewer.ui.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuhlke.logging.viewer.ui.tags.TagViewModel.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class TagFilterState(val selectedTags: Set<String>, val allTags: Set<String>)

@HiltViewModel(assistedFactory = TagViewModel.Factory::class)
class TagViewModel @AssistedInject constructor(
    @Assisted tagFilterState: TagFilterState
) : ViewModel() {

    private val selectedTags = MutableStateFlow(tagFilterState.selectedTags)
    private val allTagsSorted = tagFilterState.allTags.sorted()
    val uiState: StateFlow<UiState> = selectedTags.map { currentSelectedTags ->
        toUiState(currentSelectedTags)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        toUiState(tagFilterState.selectedTags)
    )

    private fun toUiState(currentSelectedTags: Set<String>) =
        UiState(allTagsSorted.map { name ->
            TagUiState(name, isSelected = name in currentSelectedTags)
        })

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

    @AssistedFactory
    interface Factory {
        fun create(tagFilterState: TagFilterState): TagViewModel
    }

    data class UiState(val tags: List<TagUiState>)

    data class TagUiState(val name: String, val isSelected: Boolean)
}

val UiState.selectedTags: Set<String>
    get() = tags.filter { it.isSelected }.map { it.name }.toSet()