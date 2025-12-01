package com.zuhlke.logger.logviewer.core.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zuhlke.logger.logviewer.core.ui.utils.timeFormatter
import com.zuhlke.logger.logviewer.core.utils.toClipEntry
import com.zuhlke.logging.core.data.model.LogEntry
import com.zuhlke.logging.core.data.model.Severity
import kotlinx.coroutines.launch
import kotlinx.datetime.format
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.ic_tag
import supportloggingkmp.logviewer_core.generated.resources.open_context_menu
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun LogRowWithContextMenu(
    entry: LogEntry,
    highlightsRegex: Regex,
    showLevel: Boolean,
    showTimestamp: Boolean,
    showTag: Boolean,
    expandedByDefault: Boolean,
    onExportRequested: () -> Unit,
    onSeveritySelected: () -> Unit,
    onTagSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messageText = entry.message + (entry.throwable?.let { "\n$it" } ?: "")
    val timestamp = entry.timestamp.format(timeFormatter)
    val tag = entry.tag
    val backgroundColor = when (entry.severity) {
        Severity.Warn -> Color.Yellow.copy(alpha = 0.05f)
        Severity.Error -> Color.Red.copy(alpha = 0.2f)
        Severity.Assert -> Color.Red.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val (iconChar, iconTextColor, iconBackgroundColor) = when (entry.severity) {
        Severity.Verbose -> Triple('V', Color.Black, Color(214, 214, 214))
        Severity.Debug -> Triple('D', Color.Black, Color(201, 230, 254))
        Severity.Info -> Triple('I', Color.Black, Color(215, 245, 220))
        Severity.Warn -> Triple('W', Color.Black, Color(243, 234, 194))
        Severity.Error -> Triple('E', Color.White, Color.Red.copy(alpha = 0.6f))
        Severity.Assert -> Triple('A', Color.White, Color.Red)
    }
    val matchResults = highlightsRegex.findAll(messageText)
    val annotatedMessageText = buildAnnotatedString {
        append(messageText)
        matchResults.forEach {
            addStyle(
                style = SpanStyle(background = Color.Yellow),
                start = it.range.first,
                end = it.range.last + 1
            )
        }
    }

    LogRowWithContextMenu(
        messageText = annotatedMessageText,
        timestamp = timestamp,
        tag = tag,
        severity = entry.severity.name,
        backgroundColor = backgroundColor,
        iconChar = iconChar,
        iconTextColor = iconTextColor,
        iconBackgroundColor = iconBackgroundColor,
        showLevel = showLevel,
        showTimestamp = showTimestamp,
        showTag = showTag,
        expandedByDefault = expandedByDefault,
        modifier = modifier,
        onExportRequested = onExportRequested,
        onSeveritySelected = onSeveritySelected,
        onTagSelected = onTagSelected
    )
}

@Composable
fun LogRowWithContextMenu(
    messageText: AnnotatedString,
    timestamp: String,
    tag: String,
    severity: String,
    backgroundColor: Color,
    iconChar: Char,
    iconTextColor: Color,
    iconBackgroundColor: Color,
    showLevel: Boolean,
    showTimestamp: Boolean,
    showTag: Boolean,
    expandedByDefault: Boolean,
    onExportRequested: () -> Unit,
    onSeveritySelected: () -> Unit,
    onTagSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textExpanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(expandedByDefault) {
        textExpanded = expandedByDefault
    }
    var contextMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LogRowInternal(
            messageText = messageText,
            textExpanded = textExpanded,
            iconChar = iconChar,
            iconTextColor = iconTextColor,
            iconBackgroundColor = iconBackgroundColor,
            timestamp = timestamp,
            tag = tag,
            backgroundColor = backgroundColor,
            showLevel = showLevel,
            showTimestamp = showTimestamp,
            showTag = showTag,
            onClick = { textExpanded = !textExpanded },
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                contextMenuExpanded = !contextMenuExpanded
            }
        )
        val clipboardManager = LocalClipboard.current
        val coroutineScope = rememberCoroutineScope()
        LogRowContextMenu(
            expanded = contextMenuExpanded,
            onDismissRequest = { contextMenuExpanded = false },
            onCopyToClipboard = {
                coroutineScope.launch {
                    clipboardManager.setClipEntry(messageText.toString().toClipEntry())
                }
                contextMenuExpanded = false
            },
            onExport = {
                onExportRequested()
                contextMenuExpanded = false
            },
            text = messageText.text,
            severity = severity,
            tag = tag,
            onSeveritySelected = onSeveritySelected,
            onTagSelected = onTagSelected
        )
    }
}

@Composable
fun LogRowInternal(
    messageText: AnnotatedString,
    textExpanded: Boolean,
    iconChar: Char,
    iconTextColor: Color,
    iconBackgroundColor: Color,
    timestamp: String,
    tag: String,
    backgroundColor: Color,
    showLevel: Boolean,
    showTimestamp: Boolean,
    showTag: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onLongClickLabel = stringResource(Res.string.open_context_menu)
            )
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = messageText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (textExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (showLevel || showTimestamp || showTag) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.defaultMinSize(minHeight = 24.dp)
            ) {
                if (showLevel) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(iconBackgroundColor)
                            // Make the container square based on the largest dimension of the text (supports font scaling)
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                val side = maxOf(placeable.width, placeable.height)
                                layout(side, side) {
                                    placeable.place(
                                        (side - placeable.width) / 2,
                                        (side - placeable.height) / 2
                                    )
                                }
                            }
                            .semantics {
                                role = Role.Image
                                contentDescription = ""
                            }
                    ) {
                        Text(
                            text = iconChar.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center),
                            color = iconTextColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (showTimestamp) {
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (showTag) {
                    Icon(
                        painterResource(Res.drawable.ic_tag),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
