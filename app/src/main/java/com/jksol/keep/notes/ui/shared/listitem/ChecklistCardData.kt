package com.jksol.keep.notes.ui.shared.listitem

data class ChecklistCardData(
    val transitionKey: Any,
    val title: String,
    val items: List<String>,
    val tickedItemsCount: Int,
    val isSelected: Boolean,
)