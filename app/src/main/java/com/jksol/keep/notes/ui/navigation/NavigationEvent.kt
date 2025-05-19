package com.jksol.keep.notes.ui.navigation

import com.jksol.keep.notes.ui.screens.Route

sealed class NavigationEvent {

    class NavigateBack(val result: Pair<String, Any>? = null) : NavigationEvent()

    class NavigateTo(val route: Route) : NavigationEvent()
}