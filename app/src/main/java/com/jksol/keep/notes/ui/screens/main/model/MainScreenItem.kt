package com.jksol.keep.notes.ui.screens.main.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

sealed class MainScreenItem {

    abstract val id: Long
    abstract val isPinned: Boolean
    abstract val hasScheduledReminder: Boolean
    abstract val reminderCompleted: Boolean
    abstract val title: String
    abstract val interactive: Boolean
    abstract val isSelected: Boolean
    abstract val customBackground: Color?

    val compositeKey: String by lazy { this::class.simpleName + id }

    abstract fun withSelection(isSelected: Boolean): MainScreenItem

    @Stable
    data class TextNote(
        override val id: Long,
        override val title: String,
        val content: String,
        override val isPinned: Boolean = false,
        override val hasScheduledReminder: Boolean = false,
        override val interactive: Boolean = true,
        override val isSelected: Boolean = false,
        override val reminderCompleted: Boolean = false,
        override val customBackground: Color? = null,
    ) : MainScreenItem() {

        override fun withSelection(isSelected: Boolean): MainScreenItem = copy(isSelected = isSelected)
    }

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
        override val reminderCompleted: Boolean = false,
        override val customBackground: Color? = null,
    ) : MainScreenItem() {

        override fun withSelection(isSelected: Boolean): MainScreenItem = copy(isSelected = isSelected)

        @Stable
        data class Item(val isChecked: Boolean, val text: String)
    }
}