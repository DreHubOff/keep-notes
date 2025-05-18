package com.jksol.keep.notes.ui

import com.jksol.keep.notes.ui.screens.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationEventsHost @Inject constructor() {

    private val _navigationRoute = MutableSharedFlow<Route>()
    val navigationRoute = _navigationRoute.asSharedFlow()

    suspend fun navigate(route: Route) {
        _navigationRoute.emit(route)
    }
}