package com.jksol.keep.notes.ui.screens.edit.note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState

@Composable
fun EditNoteScreen() {
    val viewModel: EditNoteViewModel = hiltViewModel()
}

@Composable
fun ScreenContent(state: EditNoteScreenState) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {

        },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Box {
//            NoteBody()
            ModificationStatusOverlay(
                navigationBarPadding = innerPadding.calculateBottomPadding(),
                message = state.modificationStatusMessage,
            )
        }
    }
}
