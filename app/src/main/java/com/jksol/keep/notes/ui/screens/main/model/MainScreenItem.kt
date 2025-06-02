package com.jksol.keep.notes.ui.screens.main.model

import androidx.compose.runtime.Stable

sealed class MainScreenItem {

    abstract val id: Long
    abstract val isPinned: Boolean
    abstract val hasScheduledReminder: Boolean
    abstract val title: String
    abstract val interactive: Boolean
    abstract val isSelected: Boolean

    val compositeKey: String by lazy { this::class.simpleName + id }

    @Stable
    data class TextNote(
        override val id: Long,
        override val title: String,
        val content: String,
        override val isPinned: Boolean = false,
        override val hasScheduledReminder: Boolean = false,
        override val interactive: Boolean = true,
        override val isSelected: Boolean = false,
    ) : MainScreenItem()

    @Stable
    data class Checklist(
        override val id: Long,
        override val title: String,
        val items: List<Item>,
        val tickedItems: Int = 0,
        val hasTickedItems: Boolean = tickedItems > 0,
        override val isPinned: Boolean = false,
        override val hasScheduledReminder: Boolean = false,
        override val interactive: Boolean = true,
        override val isSelected: Boolean = false,
    ) : MainScreenItem() {

        @Stable
        data class Item(val isChecked: Boolean, val text: String)
    }
}