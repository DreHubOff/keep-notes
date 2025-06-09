package com.jksol.keep.notes.ui.screens.edit.core

import androidx.compose.runtime.Stable
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Stable
data class ReminderEditorData(
    val isNewReminder: Boolean,

    val dateMillis: Long?,
    val dateString: String,

    val minuteOfHour: Int,
    val hourOfDay: Int,
    val timeString: String,
) {

    fun asZonedDateTime(): OffsetDateTime {
        val selectedDay = Instant.ofEpochMilli(dateMillis ?: System.currentTimeMillis())
            .atZone(ZoneOffset.UTC)
            .toOffsetDateTime()

        return OffsetDateTime.of(
            selectedDay.year,
            selectedDay.monthValue,
            selectedDay.dayOfMonth,
            hourOfDay,
            minuteOfHour,
            0,
            0,
            ZonedDateTime.now(ZoneId.systemDefault()).offset,
        )
    }
}