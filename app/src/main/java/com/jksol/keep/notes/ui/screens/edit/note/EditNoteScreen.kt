@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.edit.note

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import com.jksol.keep.notes.ui.shared.sharedBoundsTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditNoteScreen() {
    val viewModel: EditNoteViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState(EditNoteScreenState.None)

    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    val backAction = {
        focusManager.clearFocus(force = true)
        keyboardManager?.hide()
        viewModel.onBackClicked()
    }

    BackHandler {
        backAction()
    }

    if (state !is EditNoteScreenState.None) {
        ScreenContent(
            state = state as EditNoteScreenState.Idle,
            onTitleChanged = viewModel::onTitleChanged,
            onContentChanged = viewModel::onContentChanged,
            onBackClick = { backAction() },
            onPinCheckedChange = viewModel::onPinCheckedChange,
            onTitleNextClick = viewModel::onTitleNextClick,
            onDeleteClick = viewModel::moveToTrash,
        )
    }
}

@Composable
fun ScreenContent(
    state: EditNoteScreenState.Idle,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
    onTitleNextClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .sharedBoundsTransition(transitionKey = state.asTransitionKey(elementName = "card"))
        ) {
            EditActionBar(
                pinTransitionKey = state.asTransitionKey(elementName = "pin"),
                systemBarInset = innerPadding.calculateTopPadding(),
                pinned = state.isPinned,
                onBackClick = onBackClick,
                onPinCheckedChange = onPinCheckedChange,
                onDeleteClick = onDeleteClick,
            )
            Box {
                DisplayState(
                    state = state,
                    onTitleChanged = onTitleChanged,
                    onContentChanged = onContentChanged,
                    onTitleNextClick = onTitleNextClick,
                )
                ModificationDateOverlay(
                    navigationBarPadding = innerPadding.calculateBottomPadding(),
                    message = state.modificationStatusMessage,
                )
            }
        }
    }
}

@Composable
private fun DisplayState(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onTitleNextClick: () -> Unit = {},
) {
    when (state) {
        is EditNoteScreenState.Idle -> {
            NoteBody(
                modifier = Modifier,
                title = state.title,
                content = state.content,
                contentFocusRequest = state.contentFocusRequest,
                onTitleChanged = onTitleChanged,
                onContentChanged = onContentChanged,
                onTitleNextClick = onTitleNextClick,
            )
        }

        EditNoteScreenState.None -> return
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditNoteScreenStateProvider::class) state: EditNoteScreenState.Idle) {
    ApplicationTheme {
        ScreenContent(state = state)
    }
}

private class EditNoteScreenStateProvider : PreviewParameterProvider<EditNoteScreenState.Idle> {
    override val values: Sequence<EditNoteScreenState.Idle>
        get() = sequenceOf(
            EditNoteScreenState.Idle(
                noteId = 0,
                title = MainScreenDemoData.TextNotes.welcomeBanner.title,
                content = MainScreenDemoData.TextNotes.welcomeBanner.content,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.Idle(
                noteId = 1,
                modificationStatusMessage = "Edited 09:48 am",
            ),
        )
}