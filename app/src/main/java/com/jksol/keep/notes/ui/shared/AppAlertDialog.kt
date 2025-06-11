package com.jksol.keep.notes.ui.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun AppAlertDialog(
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButtonText: String? = null,
    dismissButtonText: String? = null,
    confirmAction: () -> Unit = {},
    dismissAction: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = dismissAction,
        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = title,
        icon = icon,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        text = text,
        confirmButton = {
            if (confirmButtonText != null) {
                TextButton(onClick = confirmAction) {
                    Text(
                        text = confirmButtonText,
                    )
                }
            }
        },
        dismissButton = {
            if (dismissButtonText != null) {
                TextButton(onClick = dismissAction) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSelectedNull() {
    ApplicationTheme {
        AppAlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            title = {
                Text("Empty Trash?")
            },
            text = {
                Text("This will permanently delete all items in the trash.")
            },
            confirmButtonText = stringResource(id = android.R.string.ok),
            dismissButtonText = stringResource(id = android.R.string.cancel),
            confirmAction = {},
            dismissAction = {}
        )
    }
}