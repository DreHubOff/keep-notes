package com.jksol.keep.notes.ui.screens.edit.core

import androidx.compose.runtime.Stable

@Stable
data class ReminderEditorData(
    val isNewReminder: Boolean,

    val dateMillis: Long?,
    val dateString: String,

    val minuteOfHour: Int,
    val hourOfDay: Int,
    val timeString: String,
)