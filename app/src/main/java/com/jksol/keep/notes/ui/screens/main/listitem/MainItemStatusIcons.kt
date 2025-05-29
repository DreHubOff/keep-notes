@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.main.listitem

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.sharedElementTransition

@Composable
fun MainItemStatusIcons(
    isPinned: Boolean = false,
    pinTransitionKey: Any = Unit,
    hasScheduledReminder: Boolean = false,
) {
    if (hasScheduledReminder) {
        Icon(
            imageVector = Icons.Sharp.Alarm,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    if (isPinned) {
        Icon(
            modifier = Modifier.sharedElementTransition(transitionKey = pinTransitionKey),
            painter = painterResource(R.drawable.ic_material_keep),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}