package com.jksol.keep.notes.ui.screens.edit.core

sealed class TrashSnackbarAction {

    data object Restore : TrashSnackbarAction()
    data object UndoNoteRestoration : TrashSnackbarAction()
}