package com.jksol.keep.notes.ui.screens.edit.checklist.model

import androidx.compose.runtime.Stable
import com.jksol.keep.notes.ui.shared.SnackbarEvent

@Stable
data class EditChecklistScreenState(
    val checklistId: Long,
    val title: String,
    val modificationStatusMessage: String,
    val isPinned: Boolean,
    val uncheckedItems: List<UncheckedListItemUi>,
    val checkedItems: List<CheckedListItemUi>,
    val showCheckedItems: Boolean,
    val isTrashed: Boolean,
    val showPermanentlyDeleteConfirmation: Boolean,
    val snackbarEvent: SnackbarEvent?,
    val requestItemShareType: Boolean,
) {

    companion object {
        val EMPTY = EditChecklistScreenState(
            checklistId = 0,
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
        )
    }
}