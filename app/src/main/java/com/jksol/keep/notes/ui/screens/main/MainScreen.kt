package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.MainScreenDemoData.noNotes
import com.jksol.keep.notes.MainScreenDemoData.notesList
import com.jksol.keep.notes.MainScreenDemoData.welcomeBanner
import com.jksol.keep.notes.ui.screens.main.fab.MainFabContainer
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.uiState.collectAsState()
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
    openTextNoteEditor: () -> Unit = {},
    openCheckListEditor: () -> Unit = {},
    toggleAddModeSelection: () -> Unit = {},
    onToggleSearchVisibility: () -> Unit = {},
    onNewSearchPrompt: (String) -> Unit = {},
) {
    val showOverlay = state is MainScreenState.AddModeSelection
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            MainFabContainer(
                expanded = showOverlay,
                onAddTextNoteClick = { openTextNoteEditor() },
                onAddChecklistClick = { openCheckListEditor() },
                onMainFabClicked = { toggleAddModeSelection() },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Box {
            val unpackedState = (state as? MainScreenState.AddModeSelection)?.previousState ?: state
            DisplayState(
                state = unpackedState,
                innerPadding = innerPadding,
                onToggleSearchVisibility = onToggleSearchVisibility,
                onNewSearchPrompt = onNewSearchPrompt,
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
) {
    when (state) {
        is MainScreenState.Idle -> {
            MainScreenStateIdle(
                innerPadding = innerPadding,
                listItems = state.screenItems,
                onToggleSearchVisibility = onToggleSearchVisibility
            )
            SystemBarBackground(innerPadding)
        }

        is MainScreenState.Search -> {
            MainScreenStateSearch(
                innerPadding = innerPadding,
                listItems = state.screenItems,
                onHideSearch = onToggleSearchVisibility,
                onNewPrompt = onNewSearchPrompt,
            )
        }

        else -> throw IllegalArgumentException("Unknown state: $state")
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
            MainScreenState.Idle(noNotes()),
            MainScreenState.Idle(notesList()),
            MainScreenState.Idle(welcomeBanner()),
            MainScreenState.Search(noNotes()),
            MainScreenState.Search(notesList()),
            MainScreenState.AddModeSelection(MainScreenState.Idle(welcomeBanner())),
        )
}