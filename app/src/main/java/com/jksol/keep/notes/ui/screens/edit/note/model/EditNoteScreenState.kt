package com.jksol.keep.notes.ui.screens.edit.note.model

import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.screens.edit.core.EditScreenState
import com.jksol.keep.notes.ui.screens.edit.core.ReminderEditorData
import com.jksol.keep.notes.ui.screens.edit.core.ReminderStateData
import com.jksol.keep.notes.ui.shared.SnackbarEvent

data class EditNoteScreenState(
    override val itemId: Long,
    override val modificationStatusMessage: String,
    override val isPinned: Boolean,
    override val reminderData: ReminderStateData?,
    override val reminderEditorData: ReminderEditorData?,
    override val title: String,
    override val isTrashed: Boolean,
    override val showPermanentlyDeleteConfirmation: Boolean,
    override val snackbarEvent: SnackbarEvent?,
    override val requestItemShareType: Boolean,
    override val showReminderEditorOverview: Boolean,
    override val showReminderDatePicker: Boolean,
    override val showReminderTimePicker: Boolean,
    override val showPostNotificationsPermissionPrompt: Boolean,
    override val showSetAlarmsPermissionPrompt: Boolean,
    override val background: NoteColor?,
    override val showBackgroundSelector: Boolean,
    override val backgroundColorList: List<NoteColor?>,
    val content: String,
    val contentFocusRequest: ElementFocusRequest?,
) : EditScreenState<EditNoteScreenState> {

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
    ): EditNoteScreenState = copy(
        itemId = itemId,
        title = title,
        isPinned = isPinned,
        reminderData = reminderData,
        reminderEditorData = reminderEditorData,
        isTrashed = isTrashed,
        requestItemShareType = requestItemShareType,
        modificationStatusMessage = modificationStatusMessage,
        showPermanentlyDeleteConfirmation = showPermanentlyDeleteConfirmation,
        snackbarEvent = snackbarEvent,
        showReminderEditorOverview = showReminderEditorOverview,
        showReminderDatePicker = showReminderDatePicker,
        showReminderTimePicker = showReminderTimePicker,
        showPostNotificationsPermissionPrompt = showPostNotificationsPermissionPrompt,
        showSetAlarmsPermissionPrompt = showSetAlarmsPermissionPrompt,
        background = background,
        showBackgroundSelector = showBackgroundSelector,
        backgroundColorList = backgroundColorList,
        contentFocusRequest = this@EditNoteScreenState.contentFocusRequest,
        content = this@EditNoteScreenState.content,
    )

    companion object {
        val EMPTY = EditNoteScreenState(
            itemId = -1,
            modificationStatusMessage = "",
            isPinned = false,
            contentFocusRequest = null,
            reminderData = null,
            title = "",
            content = "",
            isTrashed = false,
            showPermanentlyDeleteConfirmation = false,
            requestItemShareType = false,
            snackbarEvent = null,
            reminderEditorData = null,
            showReminderEditorOverview = false,
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