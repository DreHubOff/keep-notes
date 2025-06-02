package com.jksol.keep.notes.ui.screens.edit.checklist.model

import androidx.compose.runtime.Stable
import com.jksol.keep.notes.ui.screens.edit.core.EditScreenState
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
    override val reminderTime: String?,
    val uncheckedItems: List<UncheckedListItemUi>,
    val checkedItems: List<CheckedListItemUi>,
    val showCheckedItems: Boolean,
) : EditScreenState<EditChecklistScreenState> {

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
    ): EditChecklistScreenState = copy(
        itemId = itemId,
        title = title,
        isPinned = isPinned,
        reminderTime = reminderTime,
        isTrashed = isTrashed,
        requestItemShareType = requestItemShareType,
        modificationStatusMessage = modificationStatusMessage,
        showPermanentlyDeleteConfirmation = showPermanentlyDeleteConfirmation,
        snackbarEvent = snackbarEvent,
        uncheckedItems = uncheckedItems,
        checkedItems = checkedItems,
        showCheckedItems = showCheckedItems,
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
            reminderTime = null,
        )
    }
}