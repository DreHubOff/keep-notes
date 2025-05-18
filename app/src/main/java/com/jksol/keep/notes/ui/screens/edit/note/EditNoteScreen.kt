package com.jksol.keep.notes.ui.screens.edit.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import com.jksol.keep.notes.ui.shared.ActionBarDefaults
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditNoteScreen() {
    val viewModel: EditNoteViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState()
    ScreenContent(
        state = state.value,
        onTitleChanged = viewModel::onTitleChanged,
        onContentChanged = viewModel::onContentChanged,
    )
}

@Composable
fun ScreenContent(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ActionBar(innerPadding = innerPadding)
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
private fun ActionBar(innerPadding: PaddingValues) {
    val fullHeight = remember {
        innerPadding.calculateTopPadding() +
                ActionBarDefaults.extraPaddingTop +
                ActionBarDefaults.contentHeight
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(fullHeight)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ActionBarDefaults.contentHeight)
                .padding(horizontal = ActionBarDefaults.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = stringResource(R.string.close_search_bar_desc),
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
                title = MainScreenDemoData.TextNotes.welcomeBanner.title,
                content = MainScreenDemoData.TextNotes.welcomeBanner.content,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.Idle(
                modificationStatusMessage = "Edited 09:48 am",
            ),
        )
}