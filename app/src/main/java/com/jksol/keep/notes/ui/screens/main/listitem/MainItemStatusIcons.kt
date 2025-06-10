@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.main.listitem

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.jksol.keep.notes.R

@Composable
fun MainItemStatusIcons(
    isPinned: Boolean = false,
    reminderCompleted: Boolean = false,
    hasScheduledReminder: Boolean = false,
) {
    if (hasScheduledReminder) {
        if (reminderCompleted) {
            Icon(
                painter = painterResource(R.drawable.ic_timer_off),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Icon(
                imageVector = Icons.Sharp.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    if (isPinned) {
        Icon(
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_material_keep),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}