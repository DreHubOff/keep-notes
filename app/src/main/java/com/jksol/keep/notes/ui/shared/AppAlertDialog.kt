@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    BasicAlertDialog(
        modifier = Modifier.defaultMinSize(minWidth = 328.dp),
        onDismissRequest = dismissAction,
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 30.dp, end = 30.dp, top = 28.dp, bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (title != null) {
                        Box(Modifier.weight(1f)) {
                            title()
                        }
                    }
                    if (icon != null) {
                        icon()
                    }
                }
                if (text != null) {
                    text()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = spacedBy(16.dp, Alignment.End),
                ) {
                    if (dismissButtonText != null) {
                        TextButton(onClick = dismissAction) {
                            Text(
                                text = dismissButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    if (confirmButtonText != null) {
                        TextButton(onClick = confirmAction) {
                            Text(
                                text = confirmButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
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