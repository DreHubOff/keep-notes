package com.jksol.keep.notes.ui.screens.edit.note.model

import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.shared.SnackbarEvent

data class EditNoteScreenState(
    val noteId: Long,
    val modificationStatusMessage: String,
    val isPinned: Boolean,
    val contentFocusRequest: ElementFocusRequest?,
    val reminderTime: String?,
    val title: String,
    val content: String,
    val isTrashed: Boolean,
    val showPermanentlyDeleteConfirmation: Boolean,
    val snackbarEvent: SnackbarEvent?,
    val requestItemShareType: Boolean,
) {

    companion object {
        val EMPTY = EditNoteScreenState(
            noteId = -1,
            modificationStatusMessage = "",
            isPinned = false,
            contentFocusRequest = null,
            reminderTime = null,
            title = "",
            content = "",
            isTrashed = false,
            showPermanentlyDeleteConfirmation = false,
            requestItemShareType = false,
            snackbarEvent = null,
        )
    }
}