package com.jksol.keep.notes.ui.screens.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.TextOnlyAlertDialog

@Composable
fun ExactAlarmsPermissionDialog(
    onDismiss: () -> Unit,
    onOpenAlarmsSettings: () -> Unit,
) {
    TextOnlyAlertDialog(
        title = stringResource(R.string.allow_exact_alarms_title),
        text = stringResource(R.string.exact_alarms_permission_prompt),
        confirmButtonText = stringResource(R.string.allow),
        dismissButtonText = stringResource(R.string.deny),
        confirmAction = onOpenAlarmsSettings,
        dismissAction = onDismiss,
    )
}