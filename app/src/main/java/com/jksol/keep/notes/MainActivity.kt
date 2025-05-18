package com.jksol.keep.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jksol.keep.notes.ui.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.note.EditNoteScreen
import com.jksol.keep.notes.ui.screens.main.MainScreen
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationEventsHost: NavigationEventsHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.MainScreen().route,
                    popExitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                ) {
                    composable(Route.MainScreen().route) {
                        MainScreen()
                    }
                    composable(Route.EditNoteScreen().route) {
                        EditNoteScreen()
                    }
                }
                LaunchedEffect(navigationEventsHost) {
                    navigationEventsHost.navigationRoute.collectLatest {
                        withContext(Dispatchers.Main) {
                            navController.navigate(it.route)
                        }
                    }
                }
            }
        }
    }
}