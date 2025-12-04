package com.zuhlke.logger.logviewer.core.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zuhlke.logger.logviewer.core.ui.utils.stringResource
import com.zuhlke.logging.core.data.model.Severity
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.close
import supportloggingkmp.logviewer_core.generated.resources.filter_severity_level
import supportloggingkmp.logviewer_core.generated.resources.ic_close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SeverityModalBottomSheet(
    onDismissRequest: () -> Unit,
    selectedSeverities: Set<Severity> = emptySet(),
    onSelectionChanged: (Set<Severity>) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest, dragHandle = null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(Res.string.filter_severity_level),
                modifier = Modifier
                    .weight(1.0f)
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismissRequest) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = stringResource(Res.string.close)
                )
            }
        }
        Severity.entries.forEach {
            SeverityCheckboxRow(it, selectedSeverities, onSelectionChanged)
        }
    }
}

@Composable
private fun SeverityCheckboxRow(
    severity: Severity,
    selectedSeverities: Set<Severity>,
    onSelectionChanged: (Set<Severity>) -> Unit
) {
    val isChecked = selectedSeverities.contains(severity)
    CheckboxRow(
        text = stringResource(severity.stringResource),
        checked = isChecked
    ) { checked ->
        val newSet = if (checked) {
            selectedSeverities + severity
        } else {
            selectedSeverities - severity
        }
        onSelectionChanged(newSet)
    }
}

@Preview
@Composable
internal fun SeverityModalBottomSheetPreview() {
    SeverityModalBottomSheet(
        onDismissRequest = {},
        selectedSeverities = setOf(Severity.Error, Severity.Warn),
        onSelectionChanged = {}
    )
}
