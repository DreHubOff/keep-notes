package com.jksol.keep.notes.ui.screens.edit.checklist.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.ui.screens.edit.core.EditScreenState
import com.jksol.keep.notes.ui.screens.edit.core.ReminderEditorData
import com.jksol.keep.notes.ui.screens.edit.core.ReminderStateData
import com.jksol.keep.notes.ui.shared.SnackbarEvent

@Stable
data class EditChecklistScreenState(
    override val itemId: Long,
    override val title: String,
    override val modificationStatusMessage: String,
    override val isPinned: Boolean,
    override val isTrashed: Boolean,
    override val showPermanentlyDeleteConfirmation: Boolean,
    override val snackbarEvent: SnackbarEvent?,
    override val requestItemShareType: Boolean,
    override val reminderData: ReminderStateData?,
    override val reminderEditorData: ReminderEditorData?,
    override val showReminderEditorOverview: Boolean,
    override val showReminderDatePicker: Boolean,
    override val showReminderTimePicker: Boolean,
    override val showPostNotificationsPermissionPrompt: Boolean,
    override val showSetAlarmsPermissionPrompt: Boolean,
    override val background: NoteColor?,
    override val showBackgroundSelector: Boolean,
    override val backgroundColorList: List<NoteColor?>,
    val uncheckedItems: List<UncheckedListItemUi>,
    val checkedItems: List<CheckedListItemUi>,
    val showCheckedItems: Boolean,
) : EditScreenState<EditChecklistScreenState> {

    val focusRequests = uncheckedItems.mapNotNull { it.focusRequest }

    override fun copy(
        itemId: Long,
        title: String,
        isPinned: Boolean,
        reminderData: ReminderStateData?,
        reminderEditorData: ReminderEditorData?,
        isTrashed: Boolean,
        requestItemShareType: Boolean,
        modificationStatusMessage: String,
        showPermanentlyDeleteConfirmation: Boolean,
        snackbarEvent: SnackbarEvent?,
        showReminderEditorOverview: Boolean,
        showReminderDatePicker: Boolean,
        showReminderTimePicker: Boolean,
        showPostNotificationsPermissionPrompt: Boolean,
        showSetAlarmsPermissionPrompt: Boolean,
        background: NoteColor?,
        showBackgroundSelector: Boolean,
        backgroundColorList: List<NoteColor?>,
    ): EditChecklistScreenState = copy(
        itemId = itemId,
        title = title,
        isPinned = isPinned,
        reminderData = reminderData,
        isTrashed = isTrashed,
        requestItemShareType = requestItemShareType,
        modificationStatusMessage = modificationStatusMessage,
        showPermanentlyDeleteConfirmation = showPermanentlyDeleteConfirmation,
        snackbarEvent = snackbarEvent,
        reminderEditorData = reminderEditorData,
        showReminderEditorOverview = showReminderEditorOverview,
        showReminderDatePicker = showReminderDatePicker,
        showReminderTimePicker = showReminderTimePicker,
        showPostNotificationsPermissionPrompt = showPostNotificationsPermissionPrompt,
        showSetAlarmsPermissionPrompt = showSetAlarmsPermissionPrompt,
        background = background,
        showBackgroundSelector = showBackgroundSelector,
        backgroundColorList = backgroundColorList,
        uncheckedItems = this@EditChecklistScreenState.uncheckedItems,
        checkedItems = this@EditChecklistScreenState.checkedItems,
        showCheckedItems = this@EditChecklistScreenState.showCheckedItems,
    )

    companion object {
        val EMPTY = EditChecklistScreenState(
            itemId = 0,
            title = "",
            modificationStatusMessage = "",
            isPinned = false,
            uncheckedItems = listOf(),
            checkedItems = listOf(),
            showCheckedItems = false,
            isTrashed = false,
            showPermanentlyDeleteConfirmation = false,
            snackbarEvent = null,
            requestItemShareType = false,
            reminderData = null,
            showReminderEditorOverview = false,
            reminderEditorData = null,
            showReminderDatePicker = false,
            showReminderTimePicker = false,
            showPostNotificationsPermissionPrompt = false,
            showSetAlarmsPermissionPrompt = false,
            background = null,
            showBackgroundSelector = false,
            backgroundColorList = listOf(),
        )
    }
}