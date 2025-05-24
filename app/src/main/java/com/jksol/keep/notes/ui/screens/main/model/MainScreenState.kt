package com.jksol.keep.notes.ui.screens.main.model

import com.jksol.keep.notes.ui.shared.SnackbarEvent

data class MainScreenState(
    val screenItems: List<MainScreenItem>,
    val searchPrompt: String?,
    val addItemsMode: Boolean,
    val snackbarEvent: SnackbarEvent?,
    val searchEnabled: Boolean = searchPrompt != null,
    val isWelcomeBanner: Boolean,
) {
    companion object {
        val EMPTY: MainScreenState = MainScreenState(
            screenItems = emptyList(),
            searchPrompt = null,
            addItemsMode = false,
            snackbarEvent = null,
            isWelcomeBanner = false,
        )
    }
}