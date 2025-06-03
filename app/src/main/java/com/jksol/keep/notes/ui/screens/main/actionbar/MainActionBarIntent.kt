package com.jksol.keep.notes.ui.screens.main.actionbar

sealed class MainActionBarIntent {

    data object OpenSearch : MainActionBarIntent()
    data object HideSearch : MainActionBarIntent()

    data object OpenSideMenu : MainActionBarIntent()

    data class OpenSearchPromptChanged(val prompt: String) : MainActionBarIntent()
}