package com.jksol.keep.notes.ui.navigation

import android.content.Intent
import com.jksol.keep.notes.ui.screens.Route

sealed class NavigationEvent {

    class NavigateBack(val result: Pair<String, Any>? = null) : NavigationEvent()

    class NavigateTo(val route: Route) : NavigationEvent()

    class SendIntent(val intent: Intent) : NavigationEvent()
}