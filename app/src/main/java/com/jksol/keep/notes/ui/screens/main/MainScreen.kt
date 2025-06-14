@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.jksol.keep.notes.R
import com.jksol.keep.notes.demo_data.MainScreenDemoData.notesList
import com.jksol.keep.notes.demo_data.MainScreenDemoData.welcomeBanner
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.actionbar.MainActionBar
import com.jksol.keep.notes.ui.screens.main.actionbar.MainActionBarIntent
import com.jksol.keep.notes.ui.screens.main.drawer.MainDrawer
import com.jksol.keep.notes.ui.screens.main.fab.MainFabContainer
import com.jksol.keep.notes.ui.screens.main.listitem.MainChecklist
import com.jksol.keep.notes.ui.screens.main.listitem.MainTextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.shared.ColorSelectorDialog
import com.jksol.keep.notes.ui.shared.HandleSnackbarState
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay

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

    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(state.openSideMenuEvent) {
        if (state.openSideMenuEvent?.isHandled() == false) {
            state.openSideMenuEvent?.confirmProcessing()
            drawerState.open()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            MainDrawer(
                drawerState = drawerState,
                onTrashClick = viewModel::openTrashClick,
                onThemeClick = viewModel::openThemeSelection,
            )
        }
    ) {
        ScreenContent(
            state,
            openTextNoteEditor = viewModel::openTextNoteEditor,
            openCheckListEditor = viewModel::openCheckListEditor,
            toggleAddModeSelection = viewModel::toggleAddModeSelection,
            onSnackbarAction = viewModel::handleSnackbarAction,
            onTextNoteLongClick = viewModel::onTextNoteLongClick,
            onChecklistLongClick = viewModel::onChecklistLongClick,
            onActionBarEvent = viewModel::handleActionBarEvent,
        )
    }

    NavigationOverlay(state)

    HandleDialogs(state, viewModel)
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
    onSnackbarAction: (SnackbarEvent.Action) -> Unit = {},
    onTextNoteLongClick: (MainScreenItem.TextNote) -> Unit = {},
    onChecklistLongClick: (MainScreenItem.Checklist) -> Unit = {},
    onActionBarEvent: (MainActionBarIntent) -> Unit = {},
) {
    val showOverlay = state.addItemsMode
    val snackbarHostState = remember { SnackbarHostState() }
    HandleSnackbarState(
        snackbarHostState = snackbarHostState,
        snackbarEvent = state.snackbarEvent,
        onActionExecuted = onSnackbarAction,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
            Snackbar(it)
            }
        },
    ) { innerPadding ->
        Box {
            DisplayState(
                state = state,
                innerPadding = innerPadding,
                onActionBarEvent = onActionBarEvent,
                openTextNoteEditor = openTextNoteEditor,
                openCheckListEditor = openCheckListEditor,
                onTextNoteSelected = onTextNoteLongClick,
                onChecklistSelected = onChecklistLongClick,
            )
            if (!state.searchEnabled && !state.isSelectionMode) {
                SystemBarBackground(innerPadding)
            }
            FabsOverlay(enabled = showOverlay, onClick = { toggleAddModeSelection() })
        }
    }
}

@Composable
private fun DisplayState(
    state: MainScreenState,
    innerPadding: PaddingValues,
    onActionBarEvent: (MainActionBarIntent) -> Unit,
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit,
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit,
    onTextNoteSelected: (MainScreenItem.TextNote) -> Unit,
    onChecklistSelected: (MainScreenItem.Checklist) -> Unit,
) {
    val listScrollState = rememberLazyListState()
    val isEmptyListState = state.screenItems.isEmpty() && state !== MainScreenState.EMPTY

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        val contentPadding = remember(isEmptyListState) {
            val fabsPadding = (if (isEmptyListState) 0.dp else 120.dp)
            PaddingValues(bottom = fabsPadding + innerPadding.calculateBottomPadding())
        }
        List(
            state = state,
            containerPadding = contentPadding,
            listScrollState = listScrollState,
            onActionBarEvent = onActionBarEvent,
            onChecklistSelected = onChecklistSelected,
            openCheckListEditor = openCheckListEditor,
            onTextNoteSelected = onTextNoteSelected,
            openTextNoteEditor = openTextNoteEditor
        )
        AnimatedVisibility(
            isEmptyListState,
            enter = fadeIn(animationSpec = tween(delayMillis = 300)),
            exit = ExitTransition.None
        ) {
            NoItemsState(state = state)
        }
    }
}

@Composable
private fun List(
    containerPadding: PaddingValues,
    listScrollState: LazyListState,
    state: MainScreenState,
    onActionBarEvent: (MainActionBarIntent) -> Unit,
    onChecklistSelected: (MainScreenItem.Checklist) -> Unit,
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit,
    onTextNoteSelected: (MainScreenItem.TextNote) -> Unit,
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = containerPadding,
        verticalArrangement = spacedBy(8.dp),
        state = listScrollState,
        userScrollEnabled = state.screenItems.isNotEmpty(),
    ) {
        if (state.searchEnabled || state.isSelectionMode) {
            stickyHeader(key = "MainActionBar-sticky") {
                MainActionBar(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    onEvent = onActionBarEvent,
                )
            }
        } else {
            item(key = "MainActionBar-floating") {
                MainActionBar(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    onEvent = onActionBarEvent,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(4.dp)) }

        items(state.screenItems, key = { it.compositeKey }) { item ->
            when (item) {
                is MainScreenItem.Checklist ->
                    MainChecklist(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .animateItem(),
                        item = item,
                        onClick = {
                            if (state.isSelectionMode) {
                                onChecklistSelected(item)
                            } else {
                                openCheckListEditor(item)
                            }
                        },
                        onLongClick = { onChecklistSelected(item) },
                    )

                is MainScreenItem.TextNote ->
                    MainTextNote(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .animateItem(),
                        item = item,
                        onClick = {
                            if (state.isSelectionMode) {
                                onTextNoteSelected(item)
                            } else {
                                openTextNoteEditor(item)
                            }
                        },
                        onLongClick = { onTextNoteSelected(item) },
                    )
            }
        }
    }
}

@Composable
fun NoItemsState(state: MainScreenState) {
    val message = when {
        state.searchEnabled -> R.string.search_empty_list
        else -> R.string.notes_you_add_appear_here
    }
    val icon = if (state.searchEnabled) R.drawable.ic_no_search_results else R.drawable.ic_empty_list
    MainScreenEmptyList(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        message = stringResource(message),
        icon = painterResource(icon),
    )
}

@Composable
private fun SystemBarBackground(innerPadding: PaddingValues) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(innerPadding.calculateTopPadding())
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
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = animatedAlpha))
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

@Composable
private fun HandleDialogs(state: MainScreenState, viewModel: MainViewModel) {
    if (state.backgroundSelectionData != null) {
        ColorSelectorDialog(
            title = stringResource(R.string.note_color),
            colors = state.backgroundSelectionData.colors,
            selectedColor = state.backgroundSelectionData.selectedColor,
            onDismiss = viewModel::onHideBackgroundSelection,
            onColorSelected = viewModel::applyBackgroundToSelected,
            selectedColorUndefined = true,
        )
    }

    if (state.themeSelectorData != null) {
        ThemeSelectorDialog(
            title = stringResource(R.string.theme),
            themeOptions = state.themeSelectorData.options,
            onDismiss = viewModel::onHideThemeSelection,
            onThemeSelected = viewModel::applyTheme,
        )
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
            MainScreenState.EMPTY.copy(screenItems = welcomeBanner()),
            MainScreenState.EMPTY.copy(searchEnabled = true, searchPrompt = "Search..."),
            MainScreenState.EMPTY.copy(screenItems = notesList(), searchEnabled = true, searchPrompt = "Search..."),
            MainScreenState.EMPTY.copy(screenItems = notesList(), addItemsMode = true),
        )
}