package com.jksol.keep.notes.ui.screens.trash

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.TextOnlyAlertDialog
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun ConfirmEmptyTrashDialog(
    onEmptyTrashConfirmed: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    TextOnlyAlertDialog(
        title = stringResource(R.string.dialog_title_empty_trash),
        text = stringResource(R.string.confirm_empty_trash_message),
        confirmButtonText = stringResource(id = R.string.confirm_empty_trash),
        dismissButtonText = stringResource(R.string.cancel),
        confirmAction = onEmptyTrashConfirmed,
        dismissAction = onDismiss,
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ConfirmEmptyTrashDialog()
    }
}