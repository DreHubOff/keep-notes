package com.jksol.keep.notes.ui.screens.edit.core

import com.jksol.keep.notes.ui.shared.SnackbarEvent

interface EditScreenState<Self : EditScreenState<Self>> {
    val itemId: Long
    val title: String
    val isPinned: Boolean
    val reminderTime: String?
    val isTrashed: Boolean
    val requestItemShareType: Boolean
    val modificationStatusMessage: String
    val showPermanentlyDeleteConfirmation: Boolean
    val snackbarEvent: SnackbarEvent?

    fun copy(
        itemId: Long = this.itemId,
        title: String = this.title,
        isPinned: Boolean = this.isPinned,
        reminderTime: String? = this.reminderTime,
        isTrashed: Boolean = this.isTrashed,
        requestItemShareType: Boolean = this.requestItemShareType,
        modificationStatusMessage: String = this.modificationStatusMessage,
        showPermanentlyDeleteConfirmation: Boolean = this.showPermanentlyDeleteConfirmation,
        snackbarEvent: SnackbarEvent? = this.snackbarEvent,
    ): Self
}