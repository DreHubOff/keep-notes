package com.jksol.keep.notes.ui.screens.edit.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditNoteScreen() {
    val viewModel: EditNoteViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState(EditNoteScreenState.None)

    BackHandler {
        viewModel.onBackClicked()
    }

    ScreenContent(
        state = state.value,
        onTitleChanged = viewModel::onTitleChanged,
        onContentChanged = viewModel::onContentChanged,
        onBackClick = viewModel::onBackClicked,
        onPinCheckedChange = viewModel::onPinCheckedChange,
    )
}

@Composable
fun ScreenContent(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            EditActionBar(
                systemBarInset = innerPadding.calculateTopPadding(),
                pinned = state.isPinned,
                onBackClick = onBackClick,
                onPinCheckedChange = onPinCheckedChange
            )
            Box {
                DisplayState(
                    state = state,
                    onTitleChanged = onTitleChanged,
                    onContentChanged = onContentChanged,
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
) {
    val scrollState = rememberScrollState()

    when (state) {
        is EditNoteScreenState.Idle -> {

            NoteBody(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .verticalScroll(scrollState)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 60.dp),
                title = state.title,
                content = state.content,
                onTitleChanged = onTitleChanged,
                onContentChanged = onContentChanged,
            )
        }

        EditNoteScreenState.None -> return
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