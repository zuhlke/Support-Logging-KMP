package com.zuhlke.logging.viewer.ui.details.widgets

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zuhlke.logging.viewer.R
import com.zuhlke.logging.viewer.data.model.Severity
import com.zuhlke.logging.viewer.ui.utils.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeverityModalBottomSheet(
    onDismissRequest: () -> Unit,
    selectedSeverities: Set<Severity> = emptySet(),
    onSelectionChanged: (Set<Severity>) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest, dragHandle = null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.filter_severity_level),
                modifier = Modifier
                    .weight(1.0f)
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismissRequest) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.close)
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
fun SeverityModalBottomSheetPreview() {
    SeverityModalBottomSheet(
        onDismissRequest = {},
        selectedSeverities = setOf(Severity.Error, Severity.Warn),
        onSelectionChanged = {}
    )
}
