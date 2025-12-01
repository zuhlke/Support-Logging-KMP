package com.zuhlke.logging.viewer.ui.details.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zuhlke.logging.core.data.model.AppRun
import com.zuhlke.logging.viewer.R
import com.zuhlke.logging.viewer.ui.utils.dateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlinx.datetime.format

@OptIn(ExperimentalTime::class)
@Composable
fun AppRunHeader(appRun: AppRun, onExport: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = appRun.launchDate.format(dateTimeFormatter),
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
        Box {
            IconButton(onClick = {
                expanded = true
            }) {
                Icon(
                    painterResource(R.drawable.ic_info),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = stringResource(R.string.see_app_run_details),
                    modifier = Modifier.size(24.dp)
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
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                R.string.app_run_app_version,
                                appRun.appVersion
                            )
                        )
                    },
                    enabled = false,
                    onClick = { /* no-op */ }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.app_run_os_version, appRun.osVersion)) },
                    enabled = false,
                    onClick = { /* no-op */ }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                R.string.app_run_launch_date,
                                appRun.launchDate.format(dateTimeFormatter)
                            )
                        )
                    },
                    enabled = false,
                    onClick = { /* no-op */ }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.app_run_device, appRun.device)) },
                    enabled = false,
                    onClick = { /* no-op */ }
                )
            }
        }
    }
}
