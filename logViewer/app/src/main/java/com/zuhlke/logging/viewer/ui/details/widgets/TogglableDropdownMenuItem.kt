package com.zuhlke.logging.viewer.ui.details.widgets

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.zuhlke.logging.viewer.R

@Composable
fun TogglableDropdownMenuItem(
    text: String,
    iconId: Int,
    checked: Boolean,
    onCheckToggled: (Boolean) -> Unit
) {
    val checkedStateDescription = stringResource(R.string.checked)
    val uncheckedStateDescription = stringResource(R.string.unchecked)
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                modifier = Modifier.semantics {
                    role = Role.Checkbox
                    stateDescription =
                        if (checked) checkedStateDescription else uncheckedStateDescription
                })
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = if (checked) R.drawable.ic_check_box else R.drawable.ic_check_box_outline_blank),
                contentDescription = null
            )
        },
        onClick = { onCheckToggled(!checked) }
    )
}