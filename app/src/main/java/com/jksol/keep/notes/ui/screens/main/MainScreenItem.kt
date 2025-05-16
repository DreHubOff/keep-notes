package com.jksol.keep.notes.ui.screens.main

sealed class MainScreenItem {

    open val isPinned: Boolean = false
    open val hasAlarm: Boolean = false
    abstract val title: String

    data class TextNote(
        override val title: String,
        val content: String,
    ) : MainScreenItem()

    data class CheckList(
        override val title: String,
        val content: String,
        val items: List<Item>,
        val tickedItems: Int = 0,
        val isOverfilled: Boolean = tickedItems > 0,
    ) : MainScreenItem() {

        data class Item(val isChecked: Boolean, val text: String)
    }
}