package com.jksol.keep.notes.ui.shared.listitem

import androidx.compose.ui.graphics.Color

data class TextNoteCardData(
    val transitionKey: Any,
    val title: String,
    val content: String,
    val isSelected: Boolean,
    val customBackground: Color?,
)