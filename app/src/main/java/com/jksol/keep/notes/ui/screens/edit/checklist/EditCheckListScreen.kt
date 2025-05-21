package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jksol.keep.notes.EditChecklistDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditCheckListScreen() {
    val viewModel = hiltViewModel<EditChecklistViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle(EditChecklistScreenState())
    ScreenContent(
        state = state,
        onTitleChanged = { viewModel.onTitleChanged(it) },
        onBackClick = { viewModel.onBackClick() },
        onPinCheckedChange = { viewModel.onPinCheckedChange(it) },
        onAddChecklistItemClick = { viewModel.onAddChecklistItemClick() },
        toggleCheckedItemsVisibility = { viewModel.toggleCheckedItemsVisibility() },
        onItemUnchecked = { viewModel.onItemUnchecked(it) },
        onItemChecked = { viewModel.onItemChecked(it) },
        onItemTextChanged = { text, item -> viewModel.onItemTextChanged(text, item) },
        onDoneClicked = { viewModel.onDoneClicked() },
        onFocusStateChanged = { isFocused, item -> viewModel.onFocusStateChanged(isFocused, item) },
        onDeleteClick = { viewModel.onDeleteClick(it) },
    )
}

@Composable
fun ScreenContent(
    state: EditChecklistScreenState,
    onTitleChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onPinCheckedChange: (Boolean) -> Unit,
    onAddChecklistItemClick: () -> Unit,
    toggleCheckedItemsVisibility: () -> Unit,
    onItemUnchecked: (CheckedListItemUi) -> Unit,
    onItemChecked: (UncheckedListItemUi) -> Unit,
    onItemTextChanged: (String, UncheckedListItemUi) -> Unit,
    onDoneClicked: (UncheckedListItemUi) -> Unit,
    onFocusStateChanged: (Boolean, UncheckedListItemUi) -> Unit,
    onDeleteClick: (UncheckedListItemUi) -> Unit,
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
                    onAddChecklistItemClick = onAddChecklistItemClick,
                    toggleCheckedItemsVisibility = toggleCheckedItemsVisibility,
                    onItemUnchecked = onItemUnchecked,
                    onItemChecked = onItemChecked,
                    onItemTextChanged = onItemTextChanged,
                    onDoneClicked = onDoneClicked,
                    onFocusStateChanged = onFocusStateChanged,
                    onDeleteClick = onDeleteClick,
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
    onTitleChanged: (String) -> Unit,
    onAddChecklistItemClick: () -> Unit,
    toggleCheckedItemsVisibility: () -> Unit,
    onItemUnchecked: (CheckedListItemUi) -> Unit,
    onItemChecked: (UncheckedListItemUi) -> Unit,
    onItemTextChanged: (String, UncheckedListItemUi) -> Unit,
    onDoneClicked: (UncheckedListItemUi) -> Unit,
    onFocusStateChanged: (Boolean, UncheckedListItemUi) -> Unit,
    onDeleteClick: (UncheckedListItemUi) -> Unit,
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
        onAddChecklistItemClick = onAddChecklistItemClick,
        toggleCheckedItemsVisibility = toggleCheckedItemsVisibility,
        onItemUnchecked = onItemUnchecked,
        onItemChecked = onItemChecked,
        onItemTextChanged = onItemTextChanged,
        onDoneClicked = onDoneClicked,
        onFocusStateChanged = onFocusStateChanged,
        onDeleteClick = onDeleteClick,
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditChecklistScreenStateProvider::class) state: EditChecklistScreenState) {
    ApplicationTheme {
        ScreenContent(
            state = state,
            onTitleChanged = {},
            onBackClick = {},
            onPinCheckedChange = {},
            onAddChecklistItemClick = {},
            toggleCheckedItemsVisibility = {},
            onItemUnchecked = {},
            onItemChecked = {},
            onItemTextChanged = { _, _ -> },
            onDoneClicked = {},
            onFocusStateChanged = { _, _ -> },
            onDeleteClick = {}
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