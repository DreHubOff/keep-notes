package com.jksol.keep.notes.ui.shared

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun TextOnlyAlertDialog(
    title: String? = null,
    text: String? = null,
    confirmButtonText: String? = null,
    dismissButtonText: String? = null,
    confirmAction: () -> Unit = {},
    dismissAction: () -> Unit = {},
) {
    AppAlertDialog(
        title = if (title != null) {
            {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            null
        },
        text = if (text != null) {
            {
                Text(text = text)
            }
        } else {
            null
        },
        confirmButtonText = confirmButtonText,
        dismissButtonText = dismissButtonText,
        confirmAction = confirmAction,
        dismissAction = dismissAction,
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmEmptyTrashDialogPreview() {
    ApplicationTheme {
        TextOnlyAlertDialog(
            title = "Empty Trash?",
            text = "This will permanently delete all items in the trash.",
            confirmButtonText = stringResource(id = android.R.string.ok),
            dismissButtonText = stringResource(id = android.R.string.cancel),
        )
    }
}