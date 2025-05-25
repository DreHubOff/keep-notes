package com.jksol.keep.notes.ui.screens.edit.note.model

import androidx.compose.runtime.Stable

sealed class EditNoteScreenState {

    abstract val modificationStatusMessage: String
    abstract val isPinned: Boolean

    data object None : EditNoteScreenState() {
        override val modificationStatusMessage: String
            get() = ""
        override val isPinned: Boolean
            get() = false
    }

    @Stable
    data class Idle(
        val noteId: Long,
        val title: String = "",
        val content: String = "",
        override val modificationStatusMessage: String = "",
        val reminderTime: String? = null,
        override val isPinned: Boolean = false,
    ) : EditNoteScreenState() {

        fun asTransitionKey(elementName: String) = "${elementName}_text_note_$noteId"
    }
}