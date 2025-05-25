package com.jksol.keep.notes.ui.screens.edit.checklist.model

import androidx.compose.runtime.Stable

@Stable
data class EditChecklistScreenState(
    val checklistId: Long = 0,
    val title: String = "",
    val modificationStatusMessage: String = "",
    val isPinned: Boolean = false,
    val uncheckedItems: List<UncheckedListItemUi> = emptyList(),
    val checkedItems: List<CheckedListItemUi> = emptyList(),
    val showCheckedItems: Boolean = false,
) {

    fun asTransitionKey(elementName: String): String = "${elementName}_checklist_$checklistId"

    companion object {
        val EMPTY = EditChecklistScreenState()
    }
}