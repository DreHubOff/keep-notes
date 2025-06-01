package com.jksol.keep.notes.ui.screens.edit.note.model

sealed class TrashSnackbarAction {

    data object Restore : TrashSnackbarAction()
    data object UndoNoteRestoration : TrashSnackbarAction()
}