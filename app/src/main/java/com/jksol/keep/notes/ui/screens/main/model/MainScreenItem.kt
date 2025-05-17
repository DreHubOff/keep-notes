package com.jksol.keep.notes.ui.screens.main.model

sealed class MainScreenItem {

    abstract val isPinned: Boolean
    abstract val hasScheduledReminder: Boolean
    abstract val title: String
    abstract val interactive: Boolean

    data class TextNote(
        override val title: String,
        val content: String,
        override val isPinned: Boolean = false,
        override val hasScheduledReminder: Boolean = false,
        override val interactive: Boolean = true,
    ) : MainScreenItem()

    data class CheckList(
        override val title: String,
        val items: List<Item>,
        val tickedItems: Int = 0,
        val isOverfilled: Boolean = tickedItems > 0,
        override val isPinned: Boolean = false,
        override val hasScheduledReminder: Boolean = false,
        override val interactive: Boolean = true,
    ) : MainScreenItem() {

        data class Item(val isChecked: Boolean, val text: String)
    }
}