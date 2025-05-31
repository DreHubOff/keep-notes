package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.jksol.keep.notes.demo_data.MainScreenDemoData.notesList
import com.jksol.keep.notes.demo_data.MainScreenDemoData.welcomeBanner
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.drawer.MainDrawer
import com.jksol.keep.notes.ui.screens.main.fab.MainFabContainer
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    noteEditingResult: Route.EditNoteScreen.Result?,
    checklistEditingResult: Route.EditChecklistScreen.Result?,
) {
    val viewModel = hiltViewModel<MainViewModel>(LocalActivity.current as ComponentActivity)
    NotifyViewModelOnEditorResult(
        viewModel = viewModel,
        noteEditingResult = noteEditingResult,
        checklistEditingResult = checklistEditingResult
    )
    NotifyViewModelWhenToClearTrash(viewModel)

    val state by viewModel.uiState.collectAsState(MainScreenState.EMPTY)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            MainDrawer(
                drawerState = drawerState,
                openTrashClick = viewModel::openTrashClick,
            )
        }
    ) {
        ScreenContent(
            state,
            openTextNoteEditor = viewModel::openTextNoteEditor,
            openCheckListEditor = viewModel::openCheckListEditor,
            toggleAddModeSelection = viewModel::toggleAddModeSelection,
            onToggleSearchVisibility = viewModel::onToggleSearchVisibility,
            onNewSearchPrompt = viewModel::onNewSearchPrompt,
            onSnackbarAction = viewModel::handleSnackbarAction,
            onOpenMenuClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        )
    }
    NavigationOverlay(state)
}

@Composable
private fun NotifyViewModelOnEditorResult(
    viewModel: MainViewModel,
    noteEditingResult: Route.EditNoteScreen.Result?,
    checklistEditingResult: Route.EditChecklistScreen.Result?,
) {
    LaunchedEffect(noteEditingResult) {
        viewModel.processNoteEditingResult(noteEditingResult)
    }
    LaunchedEffect(checklistEditingResult) {
        viewModel.processChecklistEditingResult(checklistEditingResult)
    }
}

@Composable
private fun ScreenContent(
    state: MainScreenState,
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit = {},
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit = {},
    toggleAddModeSelection: () -> Unit = {},
    onToggleSearchVisibility: () -> Unit = {},
    onNewSearchPrompt: (String) -> Unit = {},
    onSnackbarAction: (SnackbarEvent.Action) -> Unit = {},
    onOpenMenuClick: () -> Unit = {},
) {
    val showOverlay = state.addItemsMode
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    handleSnackbarState(
        coroutineScope = coroutineScope,
        snackbarHostState = snackbarHostState,
        state = state,
        onActionExecuted = onSnackbarAction,
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            MainFabContainer(
                expanded = showOverlay,
                onAddTextNoteClick = { openTextNoteEditor(null) },
                onAddChecklistClick = { openCheckListEditor(null) },
                onMainFabClicked = {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    toggleAddModeSelection()
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.systemBars,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box {
            DisplayState(
                state = state,
                innerPadding = innerPadding,
                onToggleSearchVisibility = onToggleSearchVisibility,
                onNewSearchPrompt = onNewSearchPrompt,
                openTextNoteEditor = openTextNoteEditor,
                openCheckListEditor = openCheckListEditor,
                onOpenMenuClick = onOpenMenuClick,
            )
            FabsOverlay(enabled = showOverlay, onClick = { toggleAddModeSelection() })
        }
    }
}

@Composable
private fun DisplayState(
    state: MainScreenState,
    innerPadding: PaddingValues,
    onToggleSearchVisibility: () -> Unit,
    onNewSearchPrompt: (String) -> Unit,
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit,
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit,
    onOpenMenuClick: () -> Unit,
) {
    when {
        state == MainScreenState.EMPTY -> {
            MainScreenEmptyState(
                innerPadding = innerPadding,
                onToggleSearchVisibility = onToggleSearchVisibility,
                onOpenMenuClick = onOpenMenuClick,
            )
        }

        state.searchEnabled -> {
            MainScreenStateSearch(
                innerPadding = innerPadding,
                searchPrompt = state.searchPrompt,
                listItems = state.screenItems,
                onHideSearch = onToggleSearchVisibility,
                onNewPrompt = onNewSearchPrompt,
                openTextNoteEditor = openTextNoteEditor,
                openCheckListEditor = openCheckListEditor,
            )
        }

        state.isWelcomeBanner -> {
            MainScreenWelcomeBanner(
                innerPadding = innerPadding,
                banner = state.screenItems.first() as MainScreenItem.TextNote,
                onToggleSearchVisibility = onToggleSearchVisibility,
                onOpenMenuClick = onOpenMenuClick,
            )
        }

        else -> {
            MainScreenStateIdle(
                innerPadding = innerPadding,
                listItems = state.screenItems,
                onToggleSearchVisibility = onToggleSearchVisibility,
                openTextNoteEditor = openTextNoteEditor,
                openCheckListEditor = openCheckListEditor,
                onOpenMenuClick = onOpenMenuClick,
            )
            SystemBarBackground(innerPadding)
        }
    }
}

@Composable
private fun SystemBarBackground(innerPadding: PaddingValues) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(innerPadding.calculateTopPadding() + 4.dp)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
    )
}

private fun handleSnackbarState(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    state: MainScreenState,
    onActionExecuted: (SnackbarEvent.Action) -> Unit,
) {
    val snackbarEvent = state.snackbarEvent ?: return
    val action: SnackbarEvent.Action? = snackbarEvent.action
    snackbarEvent.consume()?.let { message ->
        coroutineScope.launch {
            val executionResult = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = snackbarEvent.action?.label,
                duration = if (action != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (action != null && executionResult == SnackbarResult.ActionPerformed) {
                onActionExecuted(action)
            }
        }
    }
}

@Composable
private fun NavigationOverlay(
    state: MainScreenState,
    darkOverlayAlpha: Float = 0.1f,
) {
    var overlayVisible by remember { mutableStateOf(false) }
    var overlayAlpha by remember(darkOverlayAlpha) { mutableFloatStateOf(darkOverlayAlpha) }
    val navigationOverlayDuration = remember { (defaultTransitionAnimationDuration * 0.8f).toInt() }
    LaunchedEffect(Unit, state.showNavigationOverlay) {
        if (state == MainScreenState.EMPTY) return@LaunchedEffect
        overlayVisible = true
        if (state.showNavigationOverlay?.isHandled() == false) {
            state.showNavigationOverlay.confirmProcessing()
            overlayAlpha = 0f
            delay(50)
            overlayAlpha = darkOverlayAlpha
        } else {
            overlayAlpha = darkOverlayAlpha
            delay(50)
            overlayAlpha = 0f
        }
        delay(navigationOverlayDuration.toLong())
        overlayVisible = false
    }

    if (overlayVisible) {
        val animatedAlpha by animateFloatAsState(
            targetValue = overlayAlpha,
            animationSpec = tween(durationMillis = navigationOverlayDuration)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = animatedAlpha))
        )
    }
}

@Composable
private fun NotifyViewModelWhenToClearTrash(viewModel: MainViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(state = androidx.lifecycle.Lifecycle.State.RESUMED) {
            viewModel.clearTrashOldRecords()
        }
    }
}

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=double",
    showSystemUi = true,
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=double",
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun MainScreenPreview(@PreviewParameter(PreviewBinder::class) state: MainScreenState) {
    ApplicationTheme {
        ScreenContent(state)
    }
}

private class PreviewBinder : PreviewParameterProvider<MainScreenState> {
    override val values: Sequence<MainScreenState>
        get() = sequenceOf(
            MainScreenState.EMPTY,
            MainScreenState.EMPTY.copy(screenItems = notesList()),
            MainScreenState.EMPTY.copy(screenItems = welcomeBanner(), isWelcomeBanner = true),
            MainScreenState.EMPTY.copy(searchEnabled = true, searchPrompt = "Search..."),
            MainScreenState.EMPTY.copy(screenItems = notesList(), searchEnabled = true, searchPrompt = "Search..."),
            MainScreenState.EMPTY.copy(screenItems = notesList(), addItemsMode = true),
        )
}