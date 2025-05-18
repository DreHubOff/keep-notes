package com.jksol.keep.notes.ui.screens.edit.note.model

import androidx.compose.runtime.Stable

sealed class EditNoteScreenState {

    abstract val modificationStatusMessage: String

    @Stable
    data class Idle(
        val title: String = "",
        val content: String = "",
        override val modificationStatusMessage: String = "",
        val reminderTime: String? = null,
    ) : EditNoteScreenState()
}