@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.jksol.keep.notes.demo_data.MainScreenDemoData.notesList
import com.jksol.keep.notes.demo_data.MainScreenDemoData.welcomeBanner
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.drawer.MainDrawer
import com.jksol.keep.notes.ui.screens.main.fab.MainFabContainer
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.screens.main.selection.MainScreenStateSelection
import com.jksol.keep.notes.ui.shared.HandleSnackbarState
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    noteEditingResult: Route.EditNoteScreen.Result?,
    checklistEditingResult: Route.EditChecklistScreen.Result?,
) {
    val viewModel = hiltViewModel<MainViewModel>(LocalActivity.current as ComponentActivity)

    BackHandler {
        viewModel.navigateBack()
    }

    NotifyViewModelOnEditorResult(
        viewModel = viewModel,
        noteEditingResult = noteEditingResult,
        checklistEditingResult = checklistEditingResult,
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
            },
            onTextNoteLongClick = viewModel::onTextNoteLongClick,
            onChecklistLongClick = viewModel::onChecklistLongClick,
            onExitSelectionMode = viewModel::onExitSelectionMode,
            onMoveToTrashSelected = viewModel::onMoveToTrashSelected,
            onPinnedStateChangedForSelected = viewModel::onPinnedStateChangedForSelected,
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
    onTextNoteLongClick: (MainScreenItem.TextNote) -> Unit = {},
    onChecklistLongClick: (MainScreenItem.Checklist) -> Unit = {},
    onExitSelectionMode: () -> Unit = {},
    onMoveToTrashSelected: () -> Unit = {},
    onPinnedStateChangedForSelected: (Boolean) -> Unit = {},
) {
    val showOverlay = state.addItemsMode
    val snackbarHostState = remember { SnackbarHostState() }
    HandleSnackbarState(
        snackbarHostState = snackbarHostState,
        snackbarEvent = state.snackbarEvent,
        onActionExecuted = onSnackbarAction,
    )

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
//            MainActionBar(
//                state = state,
//                topBarBehaviour = topBarBehaviour,
//                onToggleSearchVisibility = onToggleSearchVisibility,
//                onNewSearchPrompt = onNewSearchPrompt,
//            )
        }
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
                onTextNoteSelected = onTextNoteLongClick,
                onChecklistSelected = onChecklistLongClick,
                onExitSelectionMode = onExitSelectionMode,
                onMoveToTrashSelected = onMoveToTrashSelected,
                onPinnedStateChangedForSelected = onPinnedStateChangedForSelected,
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
    onExitSelectionMode: () -> Unit,
    onTextNoteSelected: (MainScreenItem.TextNote) -> Unit,
    onChecklistSelected: (MainScreenItem.Checklist) -> Unit,
    onMoveToTrashSelected: () -> Unit,
    onPinnedStateChangedForSelected: (Boolean) -> Unit,
) {
    when {
        state == MainScreenState.EMPTY -> {
            MainScreenEmptyState(
                innerPadding = innerPadding,
                onToggleSearchVisibility = onToggleSearchVisibility,
                onOpenMenuClick = onOpenMenuClick,
            )
        }

        state.isSelectionMode -> {
            MainScreenStateSelection(
                modifier = Modifier,
                innerPadding = innerPadding,
                listItems = state.screenItems,
                selectedItemCount = state.selectedItemsCount,
                onExitSelectionMode = onExitSelectionMode,
                onMoveToTrashClick = onMoveToTrashSelected,
                onPinnedStateChanged = onPinnedStateChangedForSelected,
                selectTextNote = onTextNoteSelected,
                selectChecklist = onChecklistSelected,
                selectedItemsArePinned = state.selectedItemsArePinned,
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
                selectTextNote = onTextNoteSelected,
                selectChecklist = onChecklistSelected,
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
                openChecklistEditor = openCheckListEditor,
                onOpenMenuClick = onOpenMenuClick,
                onTextNoteLongClick = onTextNoteSelected,
                onChecklistLongClick = onChecklistSelected,
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
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
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