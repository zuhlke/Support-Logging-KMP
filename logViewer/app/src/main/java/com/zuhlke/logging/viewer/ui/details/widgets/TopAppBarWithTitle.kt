package com.zuhlke.logging.viewer.ui.details.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.zuhlke.logging.viewer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithTitle(
    title: @Composable (() -> Unit),
    showLevel: Boolean,
    showTimestamp: Boolean,
    showTag: Boolean,
    expandLongMessages: Boolean,
    onShowLevelChanged: (Boolean) -> Unit,
    onShowTimestampChanged: (Boolean) -> Unit,
    onShowTagChanged: (Boolean) -> Unit,
    onExpandLongMessagesChanged: (Boolean) -> Unit,
    onExport: () -> Unit,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = title,
        scrollBehavior = scrollBehavior,
        actions = {
            var expanded by rememberSaveable { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painterResource(R.drawable.ic_more_vert),
                        contentDescription = stringResource(R.string.more_options),
                        modifier = Modifier.Companion
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.export)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            onExport()
                            expanded = false
                        }
                    )
                    HorizontalDivider()
                    TogglableDropdownMenuItem(
                        stringResource(R.string.show_level),
                        R.drawable.ic_flag,
                        showLevel,
                        onShowLevelChanged
                    )
                    TogglableDropdownMenuItem(
                        stringResource(R.string.show_timestamp),
                        R.drawable.ic_clock,
                        showTimestamp,
                        onShowTimestampChanged
                    )
                    TogglableDropdownMenuItem(
                        stringResource(R.string.show_tag),
                        R.drawable.ic_tag,
                        showTag,
                        onShowTagChanged
                    )
                    HorizontalDivider()
                    TogglableDropdownMenuItem(
                        stringResource(R.string.expand_long_messages),
                        R.drawable.ic_expand_all,
                        expandLongMessages,
                        onExpandLongMessagesChanged
                    )
                }
            }
        }
    )
}