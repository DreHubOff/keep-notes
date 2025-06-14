@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jksol.keep.notes.LocalThemeMode
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ThemeMode
import com.jksol.keep.notes.demo_data.EditChecklistDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.ShareTypeSelectionDialog
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.reminder.RemainderDatePickerDialog
import com.jksol.keep.notes.ui.screens.edit.reminder.RemainderEditorOverviewDialog
import com.jksol.keep.notes.ui.screens.edit.reminder.RemainderTimePickerDialog
import com.jksol.keep.notes.ui.screens.permissions.ExactAlarmsPermissionDialog
import com.jksol.keep.notes.ui.screens.permissions.PostNotificationsPermissionDialog
import com.jksol.keep.notes.ui.shared.ColorSelectorDialog
import com.jksol.keep.notes.ui.shared.HandleSnackbarState
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.mainItemCardTransition
import com.jksol.keep.notes.ui.shared.rememberChecklistToEditorTitleTransitionKey
import com.jksol.keep.notes.ui.shared.rememberChecklistToEditorTransitionKey
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditChecklistScreen() {
    val viewModel = hiltViewModel<EditChecklistViewModel>()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val onBackListener = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        viewModel.onBackClick()
    }

    BackHandler {
        onBackListener()
    }

    val state by viewModel.state.collectAsStateWithLifecycle(EditChecklistScreenState.EMPTY)

    val background = key(state.background, LocalThemeMode.current) {
        val isDarkTheme = LocalThemeMode.current == ThemeMode.DARK
        (if (isDarkTheme) state.background?.night else state.background?.day)?.let(::Color)
    }
    val overriddenColorTheme = key(state.background) {
        MaterialTheme.colorScheme.copy(background = background ?: MaterialTheme.colorScheme.background)
    }
    MaterialTheme(
        colorScheme = overriddenColorTheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
    ) {
        ScreenContent(
            state = state,
            onTitleChanged = viewModel::onTitleChanged,
            onBackClick = onBackListener,
            onPinCheckedChange = viewModel::onPinCheckedChange,
            onAddChecklistItemClick = viewModel::onAddChecklistItemClick,
            toggleCheckedItemsVisibility = viewModel::toggleCheckedItemsVisibility,
            onItemUnchecked = viewModel::onItemUnchecked,
            onItemChecked = viewModel::onItemChecked,
            onItemTextChanged = viewModel::onItemTextChanged,
            onDoneClicked = viewModel::onDoneClicked,
            onDeleteClick = viewModel::onDeleteClick,
            onMoveItems = viewModel::onMoveItems,
            onTitleNextClick = viewModel::onTitleNextClick,
            onMoveCompleted = viewModel::onMoveCompleted,
            onItemFocused = viewModel::onItemFocused,
            onDeleteChecklistClick = { viewModel.moveToTrash() },
            onAttemptEditTrashed = viewModel::onAttemptEditTrashed,
            onSnackbarAction = viewModel::handleSnackbarAction,
            onPermanentlyDeleteClick = viewModel::askConfirmationToPermanentlyDeleteItem,
            onRestoreClick = viewModel::restoreItemFromTrash,
            onShareClick = viewModel::onShareCurrentItemClick,
            onEditReminderClick = viewModel::onAddReminderClick,
            onSelectBackgroundClick = viewModel::showBackgroundSelection,
        )

        HandleAlerts(state, viewModel)
    }
}

@Composable
fun ScreenContent(
    state: EditChecklistScreenState,
    onTitleChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onPinCheckedChange: (Boolean) -> Unit,
    onAddChecklistItemClick: () -> Unit,
    toggleCheckedItemsVisibility: () -> Unit,
    onItemUnchecked: (CheckedListItemUi) -> Unit,
    onItemChecked: (UncheckedListItemUi) -> Unit,
    onItemTextChanged: (String, UncheckedListItemUi) -> Unit,
    onDoneClicked: (UncheckedListItemUi) -> Unit,
    onDeleteClick: (UncheckedListItemUi) -> Unit,
    onMoveItems: (fromIndex: Int, toIndex: Int) -> Unit,
    onTitleNextClick: () -> Unit,
    onMoveCompleted: () -> Unit,
    onItemFocused: (UncheckedListItemUi) -> Unit,
    onDeleteChecklistClick: () -> Unit,
    onAttemptEditTrashed: () -> Unit,
    onSnackbarAction: (SnackbarEvent.Action) -> Unit,
    onPermanentlyDeleteClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditReminderClick: () -> Unit,
    onSelectBackgroundClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    HandleSnackbarState(
        snackbarHostState = snackbarHostState,
        snackbarEvent = state.snackbarEvent,
        onActionExecuted = onSnackbarAction,
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .mainItemCardTransition(rememberChecklistToEditorTransitionKey(state.itemId)),
        contentWindowInsets = WindowInsets.systemBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            EditActionBar(
                systemBarInset = innerPadding.calculateTopPadding(),
                pinned = state.isPinned,
                trashed = state.isTrashed,
                onBackClick = onBackClick,
                onPinCheckedChange = onPinCheckedChange,
                onMoveToTrashClick = onDeleteChecklistClick,
                onPermanentlyDeleteClick = onPermanentlyDeleteClick,
                onRestoreClick = onRestoreClick,
                onShareClick = onShareClick,
                onAddReminderClick = onEditReminderClick,
                onSelectBackgroundClick = onSelectBackgroundClick,
            )
            Box(modifier = Modifier.fillMaxSize()) {
                if (state !== EditChecklistScreenState.EMPTY) {
                    Editor(
                        innerPadding,
                        state,
                        onTitleChanged,
                        onAddChecklistItemClick,
                        toggleCheckedItemsVisibility,
                        onItemUnchecked,
                        onItemChecked,
                        onItemTextChanged,
                        onDoneClicked,
                        onDeleteClick,
                        onMoveItems,
                        onTitleNextClick,
                        onMoveCompleted,
                        onItemFocused,
                        onEditReminderClick,
                    )
                }
                if (state.isTrashed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.2f))
                            .clickable(onClick = onAttemptEditTrashed)
                    )
                }
            }
        }
    }
}

@Composable
private fun Editor(
    innerPadding: PaddingValues,
    state: EditChecklistScreenState,
    onTitleChanged: (String) -> Unit,
    onAddChecklistItemClick: () -> Unit,
    toggleCheckedItemsVisibility: () -> Unit,
    onItemUnchecked: (CheckedListItemUi) -> Unit,
    onItemChecked: (UncheckedListItemUi) -> Unit,
    onItemTextChanged: (String, UncheckedListItemUi) -> Unit,
    onDoneClicked: (UncheckedListItemUi) -> Unit,
    onDeleteClick: (UncheckedListItemUi) -> Unit,
    onMoveItems: (fromIndex: Int, toIndex: Int) -> Unit,
    onTitleNextClick: () -> Unit,
    onMoveCompleted: () -> Unit,
    onItemFocused: (UncheckedListItemUi) -> Unit,
    onEditReminderClick: () -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        val paddingBottom = remember(innerPadding) { innerPadding.calculateBottomPadding() + 40.dp }
        ChecklistBody(
            modifier = Modifier
                .fillMaxSize(),
            contentPaddingBottom = paddingBottom,
            title = state.title,
            titleTransitionKey = rememberChecklistToEditorTitleTransitionKey(state.itemId),
            reminderStateData = state.reminderData,
            checkedItems = state.checkedItems,
            uncheckedItems = state.uncheckedItems,
            onTitleChanged = onTitleChanged,
            showCheckedItems = state.showCheckedItems,
            backgroundColor = state.background,
            focusRequests = state.focusRequests,
            onAddChecklistItemClick = onAddChecklistItemClick,
            toggleCheckedItemsVisibility = toggleCheckedItemsVisibility,
            onItemUnchecked = onItemUnchecked,
            onItemChecked = onItemChecked,
            onItemTextChanged = onItemTextChanged,
            onDoneClicked = onDoneClicked,
            onDeleteClick = onDeleteClick,
            onMoveItems = onMoveItems,
            onTitleNextClick = onTitleNextClick,
            onMoveCompleted = onMoveCompleted,
            onItemFocused = onItemFocused,
            onEditReminderClick = onEditReminderClick,
        )
        ModificationDateOverlay(
            navigationBarPadding = innerPadding.calculateBottomPadding(),
            message = state.modificationStatusMessage,
        )
    }
}

@Composable
private fun HandleAlerts(
    state: EditChecklistScreenState,
    viewModel: EditChecklistViewModel,
) {
    if (state.showPermanentlyDeleteConfirmation) {
        ConfirmPermanentlyDeleteChecklistDialog(
            onDeleteClick = { viewModel.permanentlyDeleteItemWhenConfirmed() },
            onDismiss = { viewModel.dismissPermanentlyDeleteConfirmation() }
        )
    }

    if (state.requestItemShareType) {
        ShareTypeSelectionDialog(
            title = stringResource(R.string.share_checklist_title),
            onDismiss = viewModel::cancelItemShareTypeRequest,
            onTypeSelected = viewModel::shareItemAs,
        )
    }


    if (state.showReminderEditorOverview && state.reminderEditorData != null) {
        RemainderEditorOverviewDialog(
            data = state.reminderEditorData,
            onDismiss = viewModel::hideReminderOverview,
            onSave = viewModel::saveReminder,
            onDelete = viewModel::deleteReminder,
            onEditDate = viewModel::editReminderDate,
            onEditTime = viewModel::editReminderTime,
        )
    }

    if (state.showReminderDatePicker && state.reminderEditorData != null) {
        RemainderDatePickerDialog(
            data = state.reminderEditorData,
            onDismiss = viewModel::hideReminderDatePicker,
            onDateSelected = viewModel::saveReminderDatePickerResult,
        )
    }

    if (state.showReminderTimePicker && state.reminderEditorData != null) {
        RemainderTimePickerDialog(
            data = state.reminderEditorData,
            onDismiss = viewModel::hideReminderTimePicker,
            onTimeSelected = viewModel::saveReminderTimePickerResult,
        )
    }

    if (state.showPostNotificationsPermissionPrompt) {
        PostNotificationsPermissionDialog(
            onDismiss = viewModel::hideReminderPermissionsPrompt,
            onGranted = { viewModel.checkReminderPermissions() },
            onOpenAppSettings = {
                viewModel.hideReminderPermissionsPrompt()
                viewModel.openAppSettings()
            }
        )
    }

    if (state.showSetAlarmsPermissionPrompt) {
        ExactAlarmsPermissionDialog(
            onDismiss = viewModel::hideReminderPermissionsPrompt,
            onOpenAlarmsSettings = {
                viewModel.hideReminderPermissionsPrompt()
                viewModel.openAlarmsSettings()
            }
        )
    }

    if (state.showBackgroundSelector) {
        ColorSelectorDialog(
            onDismiss = viewModel::hideBackgroundSelection,
            onColorSelected = viewModel::saveBackgroundColor,
            title = stringResource(R.string.note_color),
            colors = state.backgroundColorList,
            selectedColor = state.background,
        )
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditChecklistScreenStateProvider::class) state: EditChecklistScreenState) {
    ApplicationTheme {
        ScreenContent(
            state = state,
            onTitleChanged = {},
            onBackClick = {},
            onPinCheckedChange = {},
            onAddChecklistItemClick = {},
            toggleCheckedItemsVisibility = {},
            onItemUnchecked = {},
            onItemChecked = {},
            onItemTextChanged = { _, _ -> },
            onDoneClicked = {},
            onDeleteClick = {},
            onMoveItems = { _, _ -> },
            onTitleNextClick = {},
            onMoveCompleted = {},
            onItemFocused = {},
            onDeleteChecklistClick = {},
            onAttemptEditTrashed = {},
            onSnackbarAction = {},
            onPermanentlyDeleteClick = {},
            onRestoreClick = {},
            onShareClick = {},
            onEditReminderClick = {},
            onSelectBackgroundClick = {},
        )
    }
}

private class EditChecklistScreenStateProvider : PreviewParameterProvider<EditChecklistScreenState> {
    override val values: Sequence<EditChecklistScreenState>
        get() = sequenceOf(
            EditChecklistScreenState.EMPTY,
            EditChecklistScreenState.EMPTY.copy(isPinned = true),
            EditChecklistScreenState.EMPTY.copy(
                title = "Travel Checklist",
                isPinned = true,
                uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
                checkedItems = EditChecklistDemoData.checkedChecklistItems,
            ),
            EditChecklistScreenState.EMPTY.copy(
                title = "Travel Checklist",
                isPinned = true,
                checkedItems = EditChecklistDemoData.checkedChecklistItems,
                showCheckedItems = true,
                modificationStatusMessage = "Checklist modified"
            ),
            EditChecklistScreenState.EMPTY.copy(
                title = "Travel Checklist",
                checkedItems = EditChecklistDemoData.checkedChecklistItems,
                uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
                showCheckedItems = true,
                modificationStatusMessage = "Checklist modified"
            ),
        )
}