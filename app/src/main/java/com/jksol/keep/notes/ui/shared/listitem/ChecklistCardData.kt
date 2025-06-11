package com.jksol.keep.notes.ui.shared.listitem

import androidx.compose.ui.graphics.Color

data class ChecklistCardData(
    val transitionKey: Any,
    val title: String,
    val items: List<String>,
    val tickedItemsCount: Int,
    val isSelected: Boolean,
    val customBackground: Color?,
)