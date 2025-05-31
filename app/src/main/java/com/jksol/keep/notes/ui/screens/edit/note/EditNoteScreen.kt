package com.jksol.keep.notes.ui.screens.edit.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import com.jksol.keep.notes.ui.shared.mainItemCardTransition
import com.jksol.keep.notes.ui.shared.rememberTextNotePinToEditorTransitionKey
import com.jksol.keep.notes.ui.shared.rememberTextNoteToEditorTitleTransitionKey
import com.jksol.keep.notes.ui.shared.rememberTextNoteToEditorTransitionKey
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditNoteScreen() {
    val viewModel: EditNoteViewModel = hiltViewModel()

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

    val state by viewModel.state.collectAsStateWithLifecycle(EditNoteScreenState.EMPTY)
    ScreenContent(
        state = state,
        onTitleChanged = viewModel::onTitleChanged,
        onContentChanged = viewModel::onContentChanged,
        onBackClick = { backAction() },
        onPinCheckedChange = viewModel::onPinCheckedChange,
        onTitleNextClick = viewModel::onTitleNextClick,
        onMoveToTrashClick = viewModel::moveToTrash,
    )
}

@Composable
fun ScreenContent(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
    onTitleNextClick: () -> Unit = {},
    onMoveToTrashClick: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .mainItemCardTransition(rememberTextNoteToEditorTransitionKey(state.noteId)),
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            EditActionBar(
                pinTransitionKey = rememberTextNotePinToEditorTransitionKey(state.noteId),
                systemBarInset = innerPadding.calculateTopPadding(),
                pinned = state.isPinned,
                trashed = state.isTrashed,
                onBackClick = onBackClick,
                onPinCheckedChange = onPinCheckedChange,
                onMoveToTrashClick = onMoveToTrashClick,
            )
            if (state !== EditNoteScreenState.EMPTY) {
                Editor(state, onTitleChanged, onContentChanged, onTitleNextClick, innerPadding)
            }
        }
    }
}

@Composable
private fun Editor(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onTitleNextClick: () -> Unit,
    innerPadding: PaddingValues,
) {
    Box {
        NoteBody(
            modifier = Modifier,
            title = state.title,
            titleTransitionKey = rememberTextNoteToEditorTitleTransitionKey(state.noteId),
            content = state.content,
            contentFocusRequest = state.contentFocusRequest,
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

@Preview
@Composable
private fun Preview(@PreviewParameter(EditNoteScreenStateProvider::class) state: EditNoteScreenState) {
    ApplicationTheme {
        ScreenContent(state = state)
    }
}

private class EditNoteScreenStateProvider : PreviewParameterProvider<EditNoteScreenState> {
    override val values: Sequence<EditNoteScreenState>
        get() = sequenceOf(
            EditNoteScreenState.EMPTY.copy(
                noteId = 0,
                title = MainScreenDemoData.TextNotes.welcomeBanner.title,
                content = MainScreenDemoData.TextNotes.welcomeBanner.content,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.EMPTY.copy(
                noteId = 1,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.EMPTY.copy(
                noteId = 1,
                isPinned = true,
                modificationStatusMessage = "Edited 09:48 am",
            ),
        )
}