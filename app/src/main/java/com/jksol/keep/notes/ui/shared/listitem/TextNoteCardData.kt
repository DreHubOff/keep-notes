package com.jksol.keep.notes.ui.shared.listitem

import com.jksol.keep.notes.core.model.NoteColor

data class TextNoteCardData(
    val transitionKey: Any,
    val title: String,
    val content: String,
    val isSelected: Boolean,
    val customBackground: NoteColor?,
)