package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.MainScreenDemoData.notesList
import com.jksol.keep.notes.MainScreenDemoData.welcomeBanner
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.fab.MainFabContainer
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    noteEditingResult: Route.EditNoteScreen.Result?,
    checklistEditingResult: Route.EditChecklistScreen.Result?,
) {
    val viewModel = hiltViewModel<MainViewModel>(LocalActivity.current as ComponentActivity)
    viewModel.processNoteEditingResult(noteEditingResult)
    viewModel.processChecklistEditingResult(checklistEditingResult)
    val state by viewModel.uiState.collectAsState(MainScreenState.EMPTY)
    ScreenContent(
        state,
        openTextNoteEditor = viewModel::openTextNoteEditor,
        openCheckListEditor = viewModel::openCheckListEditor,
        toggleAddModeSelection = viewModel::toggleAddModeSelection,
        onToggleSearchVisibility = viewModel::onToggleSearchVisibility,
        onNewSearchPrompt = viewModel::onNewSearchPrompt,
    )
}

@Composable
private fun ScreenContent(
    state: MainScreenState,
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit = {},
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit = {},
    toggleAddModeSelection: () -> Unit = {},
    onToggleSearchVisibility: () -> Unit = {},
    onNewSearchPrompt: (String) -> Unit = {},
) {
    val showOverlay = state.addItemsMode
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    handleSnackbarState(coroutineScope, snackbarHostState, state)
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
            )
            Overlay(enabled = showOverlay, onClick = { toggleAddModeSelection() })
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
) {
    when {
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
            )
        }

        else -> {
            MainScreenStateIdle(
                innerPadding = innerPadding,
                listItems = state.screenItems,
                onToggleSearchVisibility = onToggleSearchVisibility,
                openTextNoteEditor = openTextNoteEditor,
                openCheckListEditor = openCheckListEditor,
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
) {
    val snackbarEvent = state.snackbarEvent ?: return
    snackbarEvent.consume()?.let { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
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