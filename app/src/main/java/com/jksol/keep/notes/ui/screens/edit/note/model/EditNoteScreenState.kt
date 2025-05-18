package com.jksol.keep.notes.ui.screens.edit.note.model

sealed class EditNoteScreenState {
    abstract val modificationStatusMessage: String
}