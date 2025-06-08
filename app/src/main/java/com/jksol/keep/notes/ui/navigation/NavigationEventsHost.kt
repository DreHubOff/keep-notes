package com.jksol.keep.notes.ui.navigation

import android.content.Intent
import com.jksol.keep.notes.ui.screens.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationEventsHost @Inject constructor() {

    private val _navigationRoute = MutableSharedFlow<NavigationEvent>(replay = 2, extraBufferCapacity = 2)
    val navigationRoute = _navigationRoute.asSharedFlow()

    suspend fun navigate(route: Route) {
        _navigationRoute.emit(NavigationEvent.NavigateTo(route))
    }

    suspend fun navigate(intent: Intent) {
        _navigationRoute.emit(NavigationEvent.SendIntent(intent = intent))
    }

    suspend fun popBackStack(toRoute: Route, inclusive: Boolean = false) {
        _navigationRoute.emit(NavigationEvent.PopBackStack(toRoute = toRoute, inclusive = inclusive))
    }

    suspend fun navigateBack(result: Pair<String, Any>? = null) {
        _navigationRoute.emit(NavigationEvent.NavigateBack(result = result))
    }
}