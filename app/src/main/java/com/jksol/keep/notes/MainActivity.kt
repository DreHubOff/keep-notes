@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jksol.keep.notes.ui.animation.defaultAnimationSpec
import com.jksol.keep.notes.ui.animation.scaleInFromBottomRight
import com.jksol.keep.notes.ui.animation.scaleOutToBottomRight
import com.jksol.keep.notes.ui.navigation.NavigationEvent
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.checklist.EditCheckListScreen
import com.jksol.keep.notes.ui.screens.edit.note.EditNoteScreen
import com.jksol.keep.notes.ui.screens.main.MainScreen
import com.jksol.keep.notes.ui.screens.trash.TrashScreen
import com.jksol.keep.notes.ui.shared.LocalSharedTransitionSettings
import com.jksol.keep.notes.ui.shared.SharedTransitionSettings
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.util.getAndRemove
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

private val TAG = MainActivity::class.java.simpleName

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
                SharedTransitionLayout {
                    BuildNavigationGraph(navController)
                }
                ObserveNavigationEvents(navController)
            }
        }
    }

    @Composable
    private fun SharedTransitionScope.BuildNavigationGraph(navController: NavHostController) {
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            navController = navController,
            startDestination = Route.MainScreen,
        ) {
            composable<Route.MainScreen>(
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
            ) { backStackEntry ->
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    val noteEditingResult = backStackEntry
                        .savedStateHandle
                        .getAndRemove<Route.EditNoteScreen.Result>(Route.EditNoteScreen.Result.KEY)
                    val checklistEditingResult = backStackEntry
                        .savedStateHandle
                        .getAndRemove<Route.EditChecklistScreen.Result>(Route.EditChecklistScreen.Result.KEY)
                    MainScreen(noteEditingResult, checklistEditingResult)
                }
            }
            composable<Route.EditNoteScreen>(
                enterTransition = { scaleInFromBottomRight() },
                exitTransition = { scaleOutToBottomRight() },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    EditNoteScreen()
                }
            }
            composable<Route.EditChecklistScreen>(
                enterTransition = { scaleInFromBottomRight() },
                exitTransition = { scaleOutToBottomRight() },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    EditCheckListScreen()
                }
            }
            composable<Route.TrashScreen>(
                enterTransition = { fadeIn(animationSpec = defaultAnimationSpec()) },
                exitTransition = { fadeOut(animationSpec = defaultAnimationSpec()) },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    TrashScreen()
                }
            }
        }
    }

    @Composable
    private fun ObserveNavigationEvents(navController: NavHostController) {
        LaunchedEffect(navigationEventsHost, lifecycle) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                var lastNavigationEventTime = System.currentTimeMillis()
                navigationEventsHost.navigationRoute.collectLatest { event ->
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastNavigationEventTime < 400) {
                        Log.w(TAG, "Ignoring navigation event: $event")
                        return@collectLatest
                    }
                    lastNavigationEventTime = currentTime
                    withContext(Dispatchers.Main.immediate) {
                        handleNavigationEvent(event = event, navController = navController)
                    }
                }
            }
        }
    }

    private fun handleNavigationEvent(
        event: NavigationEvent,
        navController: NavHostController,
    ) {
        when (event) {
            is NavigationEvent.NavigateBack -> {
                event.result?.let { (key, result) ->
                    navController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(key, result)
                }
                if (navController.previousBackStackEntry == null) {
                    finish()
                } else {
                    navController.popBackStack()
                }
            }

            is NavigationEvent.NavigateTo -> navController.navigate(event.route)
            is NavigationEvent.SendIntent -> {
                try {
                    startActivity(event.intent)
                } catch (error: Exception) {
                    Log.e(TAG, "Error while sending intent", error)
                }
            }
        }
    }

    @Composable
    private fun AnimatedContentScope.NavigationRoute(
        sharedTransitionScope: SharedTransitionScope,
        content: @Composable AnimatedContentScope.() -> Unit,
    ) {
        CompositionLocalProvider(
            value = LocalSharedTransitionSettings provides SharedTransitionSettings(
                transitionScope = sharedTransitionScope, animationScope = this@NavigationRoute
            )
        ) {
            content()
        }
    }
}