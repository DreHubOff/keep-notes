package com.jksol.keep.notes.ui.shared.listitem

import com.jksol.keep.notes.core.model.NoteColor

data class ChecklistCardData(
    val transitionKey: Any,
    val title: String,
    val items: List<String>,
    val tickedItemsCount: Int,
    val isSelected: Boolean,
    val customBackground: NoteColor?,
)