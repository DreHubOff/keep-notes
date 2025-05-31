package com.jksol.keep.notes.ui.screens.trash.model

sealed class UiIntent {

    data class OpenChecklistScreen(val item: TrashListItem.Checklist) : UiIntent()

    data class OpenTextNoteScreen(val item: TrashListItem.TextNote) : UiIntent()

    data object BackClicked : UiIntent()

    data object EmptyTrash : UiIntent()

    data object EmptyTrashConfirmed : UiIntent()

    data object DismissEmptyTrashConfirmation : UiIntent()
}