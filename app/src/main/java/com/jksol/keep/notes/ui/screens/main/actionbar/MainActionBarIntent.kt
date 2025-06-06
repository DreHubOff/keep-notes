package com.jksol.keep.notes.ui.screens.main.actionbar

sealed class MainActionBarIntent {

    data object OpenSearch : MainActionBarIntent()
    data object HideSearch : MainActionBarIntent()
    data class Search(val prompt: String) : MainActionBarIntent()

    data object OpenSideMenu : MainActionBarIntent()

    data object HideSelection : MainActionBarIntent()
    data class ChangePinnedStateOfSelected(val isPinned: Boolean) : MainActionBarIntent()
    data object MoveToTrashSelected : MainActionBarIntent()
}