package com.jksol.keep.notes.ui.screens.edit.checklist.model

sealed class TrashSnackbarAction {

    data object Restore : TrashSnackbarAction()
    data object UndoNoteRestoration : TrashSnackbarAction()
}