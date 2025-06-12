package com.jksol.keep.notes.ui.screens.edit.core

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.jksol.keep.notes.ui.theme.DarkOcean
import com.jksol.keep.notes.ui.theme.LightOceanMist
import java.time.OffsetDateTime

@Stable
data class ReminderStateData(
    val sourceDate: OffsetDateTime,
    val dateString: AnnotatedString,
    val outdated: Boolean,
    val reminderColorDay: Color = LightOceanMist,
    val reminderColorNight: Color = DarkOcean,
)