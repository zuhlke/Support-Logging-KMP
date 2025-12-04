package com.zuhlke.logger.logviewer.core.ui.widgets

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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.back
import supportloggingkmp.logviewer_core.generated.resources.expand_long_messages
import supportloggingkmp.logviewer_core.generated.resources.export
import supportloggingkmp.logviewer_core.generated.resources.ic_arrow_back
import supportloggingkmp.logviewer_core.generated.resources.ic_clock
import supportloggingkmp.logviewer_core.generated.resources.ic_expand_all
import supportloggingkmp.logviewer_core.generated.resources.ic_flag
import supportloggingkmp.logviewer_core.generated.resources.ic_more_vert
import supportloggingkmp.logviewer_core.generated.resources.ic_share
import supportloggingkmp.logviewer_core.generated.resources.ic_tag
import supportloggingkmp.logviewer_core.generated.resources.more_options
import supportloggingkmp.logviewer_core.generated.resources.show_level
import supportloggingkmp.logviewer_core.generated.resources.show_tag
import supportloggingkmp.logviewer_core.generated.resources.show_timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBarWithTitle(
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
                    painter = painterResource(Res.drawable.ic_arrow_back),
                    contentDescription = stringResource(Res.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = title,
        scrollBehavior = scrollBehavior,
        actions = {
            var expanded by rememberSaveable { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painterResource(Res.drawable.ic_more_vert),
                        contentDescription = stringResource(Res.string.more_options),
                        modifier = Modifier.Companion
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.export)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_share),
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
                        stringResource(Res.string.show_level),
                        Res.drawable.ic_flag,
                        showLevel,
                        onShowLevelChanged
                    )
                    TogglableDropdownMenuItem(
                        stringResource(Res.string.show_timestamp),
                        Res.drawable.ic_clock,
                        showTimestamp,
                        onShowTimestampChanged
                    )
                    TogglableDropdownMenuItem(
                        stringResource(Res.string.show_tag),
                        Res.drawable.ic_tag,
                        showTag,
                        onShowTagChanged
                    )
                    HorizontalDivider()
                    TogglableDropdownMenuItem(
                        stringResource(Res.string.expand_long_messages),
                        Res.drawable.ic_expand_all,
                        expandLongMessages,
                        onExpandLongMessagesChanged
                    )
                }
            }
        }
    )
}
