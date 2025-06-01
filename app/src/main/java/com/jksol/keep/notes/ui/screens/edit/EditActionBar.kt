@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.edit

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.PinCheckbox
import com.jksol.keep.notes.ui.shared.ThemedDropdownMenu
import com.jksol.keep.notes.ui.shared.sharedElementTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedTopAppBarColors

@Composable
fun EditActionBar(
    pinTransitionKey: Any = "",
    systemBarInset: Dp = 0.dp,
    pinned: Boolean = false,
    trashed: Boolean = false,
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
    onAddReminderClick: () -> Unit = {},
    onMoveToTrashClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onPermanentlyDeleteClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier,
        colors = themedTopAppBarColors(),
        windowInsets = WindowInsets(top = systemBarInset),
        title = { },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                )
            }
        },
        actions = {
            if (!trashed) {
                ActionsForNotTrashedItem(
                    pinned = pinned,
                    pinTransitionKey = pinTransitionKey,
                    onPinCheckedChange = onPinCheckedChange,
                    onAddReminderClick = onAddReminderClick,
                    onMoveToTrashClick = onMoveToTrashClick,
                    onShareClick = onShareClick
                )
            } else {
                ActionsForTrashedItem(
                    onDeleteClick = onPermanentlyDeleteClick,
                    onRestoreClick = onRestoreClick,
                )
            }
        }
    )
}

@Composable
private fun ActionsForNotTrashedItem(
    pinned: Boolean,
    pinTransitionKey: Any,
    onPinCheckedChange: (Boolean) -> Unit,
    onAddReminderClick: () -> Unit,
    onMoveToTrashClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    var checked by remember(pinned) { mutableStateOf(pinned) }
    PinCheckbox(
        modifier = Modifier
            .sharedElementTransition(
                transitionKey = pinTransitionKey,
            ),
        isChecked = checked,
        onCheckedChange = {
            onPinCheckedChange(it)
            checked = it
        },
        contentDescription = stringResource(R.string.pin_this_note)
    )

    ThemedDropdownMenu(
        actions = listOf(
            ThemedDropdownMenu.Action(stringResource(R.string.action_add_reminder), onClick = onAddReminderClick),
            ThemedDropdownMenu.Action(stringResource(R.string.action_delete), onClick = onMoveToTrashClick),
            ThemedDropdownMenu.Action(stringResource(R.string.action_share), onClick = onShareClick),
        )
    )
}

@Composable
private fun ActionsForTrashedItem(
    onDeleteClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {},
) {
    IconButton(onClick = onDeleteClick) {
        Icon(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = stringResource(R.string.action_delete),
        )
    }
    IconButton(onClick = onRestoreClick) {
        Icon(
            imageVector = Icons.Rounded.SettingsBackupRestore,
            contentDescription = stringResource(R.string.restore),
        )
    }
}

@Preview
@Composable
private fun PreviewPinned() {
    ApplicationTheme {
        EditActionBar(
            systemBarInset = 10.dp,
            pinned = true,
        )
    }
}

@Preview
@Composable
private fun PreviewNotPinned() {
    ApplicationTheme {
        EditActionBar(
            systemBarInset = 10.dp,
            pinned = true,
        )
    }
}

@Preview
@Composable
private fun PreviewTrashed() {
    ApplicationTheme {
        EditActionBar(
            systemBarInset = 10.dp,
            trashed = true,
        )
    }
}