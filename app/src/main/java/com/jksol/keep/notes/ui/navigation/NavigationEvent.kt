package com.jksol.keep.notes.ui.navigation

import com.jksol.keep.notes.ui.screens.Route

// Must be unique per instance
@Suppress("CanSealedSubClassBeObject")
sealed class NavigationEvent {

    class NavigateBack : NavigationEvent()

    class NavigateTo(val route: Route) : NavigationEvent()
}