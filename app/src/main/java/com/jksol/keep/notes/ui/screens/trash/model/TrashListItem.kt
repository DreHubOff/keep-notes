package com.jksol.keep.notes.ui.screens.trash.model

import androidx.compose.runtime.Stable

sealed class TrashListItem {

    abstract val id: Long
    abstract val title: String
    abstract val daysLeftMessage: String

    val compositeKey: String by lazy { this::class.simpleName + id }

    abstract fun asTransitionKey(elementName: String): String

    @Stable
    data class TextNote(
        override val id: Long,
        override val title: String,
        val content: String,
        override val daysLeftMessage: String,
    ) : TrashListItem() {
        override fun asTransitionKey(elementName: String): String = "${elementName}_text_note_$id"
    }

    @Stable
    data class Checklist(
        override val id: Long,
        override val title: String,
        val items: List<Item>,
        val tickedItems: Int = 0,
        val hasTickedItems: Boolean = tickedItems > 0,
        override val daysLeftMessage: String,
    ) : TrashListItem() {

        @Stable
        data class Item(val text: String)

        override fun asTransitionKey(elementName: String): String = "${elementName}_checklist_$id"
    }
}