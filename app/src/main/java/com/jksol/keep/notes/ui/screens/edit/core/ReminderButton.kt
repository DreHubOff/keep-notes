package com.jksol.keep.notes.ui.screens.edit.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.LocalThemeMode
import com.jksol.keep.notes.ThemeMode

private val reminderTranslationX = (-4).dp

@Composable
fun ReminderButton(
    modifier: Modifier,
    reminderData: ReminderStateData,
    onClick: () -> Unit,
) {
    val reminderColor = if (LocalThemeMode.current == ThemeMode.DARK) {
        reminderData.reminderColorNight
    } else {
        reminderData.reminderColorDay
    }
    Button(
        modifier = modifier.graphicsLayer {
            translationX = reminderTranslationX.toPx()
        },
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = reminderColor,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    ) {
        Icon(imageVector = Icons.Sharp.Alarm, contentDescription = null)
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = reminderData.dateString,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.let {
                if (reminderData.outdated) it.copy(alpha = 0.6f) else it
            }
        )
    }
}