@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.edit.reminder

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.edit.core.ReminderEditorData
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedTimePickerColors

@Composable
fun RemainderTimePickerDialog(
    data: ReminderEditorData,
    onDismiss: () -> Unit = {},
    onTimeSelected: (TimePickerState) -> Unit = {},
) {
    val timePickerState = rememberTimePickerState(
        initialHour = data.hourOfDay,
        initialMinute = data.minuteOfHour,
        is24Hour = false,
    )

    var showDial by remember { mutableStateOf(false) }

    val toggleIcon = if (showDial) {
        Icons.Filled.EditCalendar
    } else {
        Icons.Filled.AccessTime
    }

    AdvancedTimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onTimeSelected(timePickerState) },
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(imageVector = toggleIcon, contentDescription = null)
            }
        },
    ) {
        if (showDial) {
            TimePicker(
                state = timePickerState,
                colors = themedTimePickerColors()
            )
        } else {
            TimeInput(
                state = timePickerState,
                colors = themedTimePickerColors()
            )
        }
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = stringResource(R.string.enter_time_title),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) = Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 6.dp,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .background(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface
            ),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                toggle()
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) {
                    Text(
                        stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                TextButton(onClick = onConfirm) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        RemainderTimePickerDialog(
            data = ReminderEditorData(
                isNewReminder = true,
                dateString = "14 May, 2025",
                timeString = "3:00 pm",
                dateMillis = null,
                minuteOfHour = 20,
                hourOfDay = 8,
            ),
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun PreviewDark() {
    ApplicationTheme {
        RemainderTimePickerDialog(
            data = ReminderEditorData(
                isNewReminder = true,
                dateString = "14 May, 2025",
                timeString = "3:00 pm",
                dateMillis = null,
                minuteOfHour = 20,
                hourOfDay = 8,
            ),
        )
    }
}