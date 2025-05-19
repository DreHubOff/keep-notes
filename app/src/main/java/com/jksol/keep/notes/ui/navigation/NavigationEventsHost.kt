package com.jksol.keep.notes.ui.navigation

import com.jksol.keep.notes.ui.screens.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationEventsHost @Inject constructor() {

    private val _navigationRoute = MutableSharedFlow<NavigationEvent>()
    val navigationRoute = _navigationRoute.asSharedFlow()

    suspend fun navigate(route: Route) {
        _navigationRoute.emit(NavigationEvent.NavigateTo(route))
    }

    suspend fun navigateBack(result: Pair<String, Any>? = null) {
        _navigationRoute.emit(NavigationEvent.NavigateBack(result = result))
    }
}