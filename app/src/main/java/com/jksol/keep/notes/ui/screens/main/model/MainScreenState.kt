package com.jksol.keep.notes.ui.screens.main.model

import com.jksol.keep.notes.ui.shared.SnackbarEvent

interface StateWithList {
    val screenItems: List<MainScreenItem>
}

interface SnackbarEventDelivery {
    val snackbarEvent: SnackbarEvent?
    fun withSnackbarEvent(snackbarEvent: SnackbarEvent?): MainScreenState
}

sealed class MainScreenState : SnackbarEventDelivery {

    data class None(override val snackbarEvent: SnackbarEvent? = null) : MainScreenState() {
        override fun withSnackbarEvent(snackbarEvent: SnackbarEvent?): MainScreenState = copy(snackbarEvent = snackbarEvent)
    }

    data class Idle(
        override val screenItems: List<MainScreenItem>,
        override val snackbarEvent: SnackbarEvent? = null,
    ) : MainScreenState(), StateWithList {

        override fun withSnackbarEvent(snackbarEvent: SnackbarEvent?): MainScreenState =
            copy(snackbarEvent = snackbarEvent)
    }

    data class Search(
        override val screenItems: List<MainScreenItem>,
        override val snackbarEvent: SnackbarEvent? = null,
    ) : MainScreenState(), StateWithList {

        override fun withSnackbarEvent(snackbarEvent: SnackbarEvent?): MainScreenState =
            copy(snackbarEvent = snackbarEvent)
    }

    data class AddModeSelection(
        val previousState: MainScreenState,
        override val snackbarEvent: SnackbarEvent? = null,
    ) : MainScreenState() {
        override fun withSnackbarEvent(snackbarEvent: SnackbarEvent?): MainScreenState =
            copy(snackbarEvent = snackbarEvent)
    }

    data class WelcomeBanner(
        val textNote: MainScreenItem.TextNote,
        override val snackbarEvent: SnackbarEvent? = null,
    ) : MainScreenState() {
        override fun withSnackbarEvent(snackbarEvent: SnackbarEvent?): MainScreenState =
            copy(snackbarEvent = snackbarEvent)
    }
}