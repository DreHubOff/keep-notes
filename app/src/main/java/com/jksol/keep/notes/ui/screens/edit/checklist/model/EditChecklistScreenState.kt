package com.jksol.keep.notes.ui.screens.edit.checklist.model

import androidx.compose.runtime.Stable
import com.jksol.keep.notes.core.model.ChecklistItem

@Stable
data class EditChecklistScreenState(
    val checklistId: Long = 0,
    val title: String = "",
    val modificationStatusMessage: String = "",
    val isPinned: Boolean = false,
    val uncheckedItems: List<ChecklistItem> = emptyList(),
    val checkedItems: List<ChecklistItem> = emptyList(),
    val showCheckedItems: Boolean = false,
)