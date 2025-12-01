package com.zuhlke.logger.logviewer.core.ui.widgets

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import supportloggingkmp.logviewer_core.generated.resources.Res
import supportloggingkmp.logviewer_core.generated.resources.checked
import supportloggingkmp.logviewer_core.generated.resources.ic_check_box
import supportloggingkmp.logviewer_core.generated.resources.ic_check_box_outline_blank
import supportloggingkmp.logviewer_core.generated.resources.unchecked

@Composable
fun TogglableDropdownMenuItem(
    text: String,
    iconId: DrawableResource,
    checked: Boolean,
    onCheckToggled: (Boolean) -> Unit
) {
    val checkedStateDescription = stringResource(Res.string.checked)
    val uncheckedStateDescription = stringResource(Res.string.unchecked)
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                modifier = Modifier.semantics {
                    role = Role.Checkbox
                    stateDescription =
                        if (checked) checkedStateDescription else uncheckedStateDescription
                }
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(
                    if (checked) {
                        Res.drawable.ic_check_box
                    } else {
                        Res.drawable.ic_check_box_outline_blank
                    }
                ),
                contentDescription = null
            )
        },
        onClick = { onCheckToggled(!checked) }
    )
}
