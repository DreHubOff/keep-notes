package com.jksol.keep.notes.ui.screens.edit.note.model

import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.screens.edit.core.EditScreenState
import com.jksol.keep.notes.ui.shared.SnackbarEvent

data class EditNoteScreenState(
    override val itemId: Long,
    override val modificationStatusMessage: String,
    override val isPinned: Boolean,
    override val reminderTime: String?,
    override val title: String,
    override val isTrashed: Boolean,
    override val showPermanentlyDeleteConfirmation: Boolean,
    override val snackbarEvent: SnackbarEvent?,
    override val requestItemShareType: Boolean,
    val content: String,
    val contentFocusRequest: ElementFocusRequest?,
) : EditScreenState<EditNoteScreenState> {

    override fun copy(
        itemId: Long,
        title: String,
        isPinned: Boolean,
        reminderTime: String?,
        isTrashed: Boolean,
        requestItemShareType: Boolean,
        modificationStatusMessage: String,
        showPermanentlyDeleteConfirmation: Boolean,
        snackbarEvent: SnackbarEvent?,
    ): EditNoteScreenState = copy(
        itemId = itemId,
        title = title,
        isPinned = isPinned,
        reminderTime = reminderTime,
        isTrashed = isTrashed,
        requestItemShareType = requestItemShareType,
        modificationStatusMessage = modificationStatusMessage,
        showPermanentlyDeleteConfirmation = showPermanentlyDeleteConfirmation,
        snackbarEvent = snackbarEvent,
        contentFocusRequest = this.contentFocusRequest,
        content = this.content,
    )

    companion object {
        val EMPTY = EditNoteScreenState(
            itemId = -1,
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