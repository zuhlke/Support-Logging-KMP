package com.zuhlke.logging.viewer.ui.details.widgets

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.zuhlke.logging.viewer.R

@Composable
fun LogRowContextMenu(
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
            text = { Text(stringResource(R.string.copy_message)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_content_copy),
                    contentDescription = null
                )
            },
            onClick = onCopyToClipboard
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.export)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = null
                )
            },
            onClick = onExport
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.show_similar_items)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_visibility),
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
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
                    painter = painterResource(id = R.drawable.ic_short_text),
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
                    painter = painterResource(id = R.drawable.ic_flag),
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
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = null
                )
            },
            onClick = onTagSelected
        )
    }
}
