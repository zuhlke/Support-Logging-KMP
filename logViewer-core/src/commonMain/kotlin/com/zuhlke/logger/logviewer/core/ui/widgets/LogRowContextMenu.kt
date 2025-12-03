package com.zuhlke.logger.logviewer.core.ui.widgets

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.copy_message
import supportloggingkmp.logviewer_core.generated.resources.export
import supportloggingkmp.logviewer_core.generated.resources.ic_arrow_right
import supportloggingkmp.logviewer_core.generated.resources.ic_content_copy
import supportloggingkmp.logviewer_core.generated.resources.ic_flag
import supportloggingkmp.logviewer_core.generated.resources.ic_share
import supportloggingkmp.logviewer_core.generated.resources.ic_short_text
import supportloggingkmp.logviewer_core.generated.resources.ic_tag
import supportloggingkmp.logviewer_core.generated.resources.ic_visibility
import supportloggingkmp.logviewer_core.generated.resources.show_similar_items

@Composable
internal fun LogRowContextMenu(
    expanded: Boolean,
    onCopyToClipboard: () -> Unit,
    onExport: () -> Unit,
    onDismissRequest: () -> Unit,
    text: String,
    severity: String,
    tag: String,
    onSeveritySelected: () -> Unit,
    onTagSelected: () -> Unit
) {
    var expandedNested by rememberSaveable { mutableStateOf(false) }
    MainMenu(
        expanded = expanded && !expandedNested,
        onCopyToClipboard = onCopyToClipboard,
        onExport = onExport,
        onDismissRequest = onDismissRequest,
        onShowSimilarClick = {
            expandedNested = true
        }
    )
    SimilarItemsMenu(
        showSimilarItemsSubMenuShown = expanded && expandedNested,
        onDismiss = {
            expandedNested = false
        },
        text = text,
        severity = severity,
        tag = tag,
        onSeveritySelected = {
            onSeveritySelected()
            onDismissRequest()
        },
        onTagSelected = {
            onTagSelected()
            onDismissRequest()
        }
    )
}

@Composable
private fun MainMenu(
    expanded: Boolean,
    onCopyToClipboard: () -> Unit,
    onExport: () -> Unit,
    onDismissRequest: () -> Unit,
    onShowSimilarClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.copy_message)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_content_copy),
                    contentDescription = null
                )
            },
            onClick = onCopyToClipboard
        )
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.export)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_share),
                    contentDescription = null
                )
            },
            onClick = onExport
        )
        DropdownMenuItem(
            text = { Text(stringResource(Res.string.show_similar_items)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_visibility),
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = null
                )
            },
            onClick = onShowSimilarClick
        )
    }
}

@Composable
private fun SimilarItemsMenu(
    showSimilarItemsSubMenuShown: Boolean,
    onDismiss: () -> Unit,
    text: String,
    severity: String,
    tag: String,
    onSeveritySelected: () -> Unit,
    onTagSelected: () -> Unit
) {
    DropdownMenu(
        expanded = showSimilarItemsSubMenuShown,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = {
                Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_short_text),
                    contentDescription = null
                )
            },
            onClick = {
                // TODO: Do something
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = {
                Text(severity)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_flag),
                    contentDescription = null
                )
            },
            onClick = onSeveritySelected
        )
        DropdownMenuItem(
            text = {
                Text(tag)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_tag),
                    contentDescription = null
                )
            },
            onClick = onTagSelected
        )
    }
}
