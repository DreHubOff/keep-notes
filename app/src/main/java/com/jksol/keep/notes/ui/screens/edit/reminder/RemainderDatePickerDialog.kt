@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.edit.reminder

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.edit.core.ReminderEditorData
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedDatePickerColors
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun RemainderDatePickerDialog(
    data: ReminderEditorData,
    onDismiss: () -> Unit = {},
    onDateSelected: (Long?) -> Unit = {},
) {
    val currentYear = remember { LocalDate.now(ZoneOffset.UTC).year }
    val supportedYearRange = remember { currentYear..currentYear + 100 }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = data.dateMillis,
        selectableDates = object : SelectableDates {

            private val todayUtcStartMillis = LocalDate.now(ZoneOffset.UTC)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()

            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayUtcStartMillis
            }

            override fun isSelectableYear(year: Int): Boolean = year >= currentYear
        },
        yearRange = supportedYearRange,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = themedDatePickerColors()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        RemainderDatePickerDialog(
            data = ReminderEditorData(
                isNewReminder = true,
                dateString = "14 May, 2025",
                timeString = "3:00 pm",
                dateMillis = null,
                minuteOfHour = 4,
                hourOfDay = 3,
            ),
        )
    }
}