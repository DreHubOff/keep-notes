package com.jksol.keep.notes.ui.navigation

import android.content.Intent
import com.jksol.keep.notes.ui.screens.Route

sealed class NavigationEvent {

    var consumed = false

    class NavigateBack(val result: Pair<String, Any>? = null) : NavigationEvent()

    class NavigateTo(val route: Route) : NavigationEvent()

    class PopBackStack(val toRoute: Route, val inclusive: Boolean = false) : NavigationEvent()

    class SendIntent(val intent: Intent) : NavigationEvent()
}