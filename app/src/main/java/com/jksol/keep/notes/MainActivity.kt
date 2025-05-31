@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jksol.keep.notes.ui.navigation.NavigationEvent
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.checklist.EditCheckListScreen
import com.jksol.keep.notes.ui.screens.edit.note.EditNoteScreen
import com.jksol.keep.notes.ui.screens.main.MainScreen
import com.jksol.keep.notes.ui.shared.LocalSharedTransitionSettings
import com.jksol.keep.notes.ui.shared.SharedTransitionSettings
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
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

    private val defaultScreenEnterAnimation by lazy {
        scaleIn(
            animationSpec = tween(durationMillis = defaultTransitionAnimationDuration),
            transformOrigin = TransformOrigin(1f, 1f)
        )
    }

    private val defaultScreenExitAnimation by lazy {
        scaleOut(
            animationSpec = tween(durationMillis = defaultTransitionAnimationDuration),
            transformOrigin = TransformOrigin(1f, 1f)
        )
    }

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
                        .get<Route.EditNoteScreen.Result>(Route.EditNoteScreen.Result.KEY)
                    val checklistEditingResult = backStackEntry
                        .savedStateHandle
                        .get<Route.EditChecklistScreen.Result>(Route.EditChecklistScreen.Result.KEY)
                    MainScreen(noteEditingResult, checklistEditingResult)
                }
            }
            composable<Route.EditNoteScreen>(
                enterTransition = { defaultScreenEnterAnimation },
                exitTransition = { defaultScreenExitAnimation },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    EditNoteScreen()
                }
            }
            composable<Route.EditChecklistScreen>(
                enterTransition = { defaultScreenEnterAnimation },
                exitTransition = { defaultScreenExitAnimation },
            ) {
                NavigationRoute(sharedTransitionScope = this@BuildNavigationGraph) {
                    EditCheckListScreen()
                }
            }
        }
    }

    @Composable
    private fun ObserveNavigationEvents(navController: NavHostController) {
        LaunchedEffect(navigationEventsHost, lifecycle) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                var lastBackEventTime = System.currentTimeMillis()
                navigationEventsHost.navigationRoute.collectLatest { event ->
                    withContext(Dispatchers.Main.immediate) {
                        when (event) {
                            is NavigationEvent.NavigateBack -> {
                                if (System.currentTimeMillis() - lastBackEventTime < 800) {
                                    return@withContext
                                }
                                event.result?.let { (key, result) ->
                                    navController
                                        .previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(key, result)
                                }
                                navController.popBackStack()
                                lastBackEventTime = System.currentTimeMillis()
                            }

                            is NavigationEvent.NavigateTo -> navController.navigate(event.route)
                        }
                    }
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