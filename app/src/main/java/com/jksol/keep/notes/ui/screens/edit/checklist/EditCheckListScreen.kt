package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.EditChecklistDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditCheckListScreen() {

    ScreenContent(
        state = EditChecklistScreenState(
            isPinned = true,
            uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
        ),
    )
}

@Composable
fun ScreenContent(
    state: EditChecklistScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
    state: EditChecklistScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
) {
    ChecklistBody(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        title = state.title,
        checkedItems = state.checkedItems,
        uncheckedItems = state.uncheckedItems,
        onTitleChanged = onTitleChanged,
        showCheckedItems = state.showCheckedItems,
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditChecklistScreenStateProvider::class) state: EditChecklistScreenState) {
    ApplicationTheme {
        ScreenContent(
            state = state
        )
    }
}

private class EditChecklistScreenStateProvider : PreviewParameterProvider<EditChecklistScreenState> {
    override val values: Sequence<EditChecklistScreenState>
        get() = sequenceOf(
            EditChecklistScreenState(),
            EditChecklistScreenState(isPinned = true),
            EditChecklistScreenState(
                title = "Travel Checklist",
                isPinned = true,
                uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
                checkedItems = EditChecklistDemoData.checkedChecklistItems,
            ),
            EditChecklistScreenState(
                title = "Travel Checklist",
                isPinned = true,
                checkedItems = EditChecklistDemoData.checkedChecklistItems,
                showCheckedItems = true,
                modificationStatusMessage = "Checklist modified"
            ),
            EditChecklistScreenState(
                title = "Travel Checklist",
                checkedItems = EditChecklistDemoData.checkedChecklistItems,
                uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
                showCheckedItems = true,
                modificationStatusMessage = "Checklist modified"
            ),
        )
}