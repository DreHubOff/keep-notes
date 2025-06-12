package com.jksol.keep.notes.ui.screens.trash.model

import androidx.compose.runtime.Stable
import com.jksol.keep.notes.core.model.NoteColor

sealed class TrashListItem {

    abstract val id: Long
    abstract val title: String
    abstract val daysLeftMessage: String
    abstract val customBackground: NoteColor?

    val compositeKey: String by lazy { this::class.simpleName + id }

    @Stable
    data class TextNote(
        override val id: Long,
        override val title: String,
        val content: String,
        override val daysLeftMessage: String,
        override val customBackground: NoteColor?,
    ) : TrashListItem()

    @Stable
    data class Checklist(
        override val id: Long,
        override val title: String,
        val items: List<String>,
        val tickedItems: Int = 0,
        val hasTickedItems: Boolean = tickedItems > 0,
        override val daysLeftMessage: String,
        override val customBackground: NoteColor?,
    ) : TrashListItem()
}