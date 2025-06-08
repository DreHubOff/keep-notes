package com.jksol.keep.notes.ui.screens.edit.reminder

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AccessTime
import androidx.compose.material.icons.sharp.ArrowDropDown
import androidx.compose.material.icons.sharp.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.edit.core.ReminderEditorData
import com.jksol.keep.notes.ui.shared.AppAlertDialog
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun RemainderEditorOverviewDialog(
    data: ReminderEditorData,
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEditDate: () -> Unit = {},
    onEditTime: () -> Unit = {},
) {
    val dialogTitle = if (data.isNewReminder) {
        stringResource(R.string.action_add_reminder)
    } else {
        stringResource(R.string.action_edit_reminder)
    }
    AppAlertDialog(
        dismissAction = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = dialogTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                if (!data.isNewReminder) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        },
        text = {
            Column {
                DateTimeSelectionButton(text = data.dateString, icon = Icons.Sharp.Event, onClick = onEditDate)
                DateTimeSelectionButton(text = data.timeString, icon = Icons.Sharp.AccessTime, onClick = onEditTime)
            }
        },
        dismissButtonText = stringResource(R.string.cancel),
        confirmButtonText = stringResource(R.string.save),
        confirmAction = onSave,
    )
}

@Composable
private fun DateTimeSelectionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        contentPadding = buttonContentPadding,
        colors = buttonColors(),
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Text(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .weight(1f),
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
            )
        )
        Icon(
            modifier = Modifier.padding(end = 12.dp),
            imageVector = Icons.Sharp.ArrowDropDown,
            contentDescription = null
        )
    }
}

@Composable
private fun buttonColors() = ButtonColors(
    containerColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledContainerColor = MaterialTheme.colorScheme.onSurface,
    disabledContentColor = MaterialTheme.colorScheme.onSurface,
)

private val buttonContentPadding = PaddingValues(
    horizontal = 0.dp,
    vertical = 8.dp
)

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        RemainderEditorOverviewDialog(
            data = ReminderEditorData(
                isNewReminder = true,
                dateMillis = System.currentTimeMillis(),
                dateString = "14 May, 2025",
                timeString = "3:00 pm",
                minuteOfHour = 30,
                hourOfDay = 7,
            ),
        )
    }
}