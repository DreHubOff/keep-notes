package com.jksol.keep.notes.ui.shared

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans

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
                Text(
                    modifier = Modifier.padding(bottom = 14.dp),
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = plusJakartaSans,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            null
        },
        text = if (text != null) {
            {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = plusJakartaSans,
                    color = MaterialTheme.colorScheme.onSurface,
                )
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