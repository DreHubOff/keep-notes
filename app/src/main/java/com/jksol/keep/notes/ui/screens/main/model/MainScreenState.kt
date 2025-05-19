package com.jksol.keep.notes.ui.screens.main.model

interface StateWithList {
    val screenItems: List<MainScreenItem>
}

sealed class MainScreenState {

    data object None : MainScreenState()

    data class Idle(override val screenItems: List<MainScreenItem>) : MainScreenState(), StateWithList

    data class Search(override val screenItems: List<MainScreenItem>) : MainScreenState(), StateWithList

    data class AddModeSelection(val previousState: MainScreenState) : MainScreenState()
}