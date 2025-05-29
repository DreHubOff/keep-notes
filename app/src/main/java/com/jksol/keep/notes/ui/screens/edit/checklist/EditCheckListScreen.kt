@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jksol.keep.notes.demo_data.EditChecklistDemoData
import com.jksol.keep.notes.ui.screens.edit.EditActionBar
import com.jksol.keep.notes.ui.screens.edit.ModificationDateOverlay
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.shared.sharedBoundsTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditCheckListScreen() {
    val viewModel = hiltViewModel<EditChecklistViewModel>()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val onBackListener = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        viewModel.onBackClick()
    }

    BackHandler {
        onBackListener()
    }

    val state by viewModel.state.collectAsStateWithLifecycle(EditChecklistScreenState.EMPTY)
    ScreenContent(
        state = state,
        onTitleChanged = viewModel::onTitleChanged,
        onBackClick = onBackListener,
        onPinCheckedChange = viewModel::onPinCheckedChange,
        onAddChecklistItemClick = viewModel::onAddChecklistItemClick,
        toggleCheckedItemsVisibility = viewModel::toggleCheckedItemsVisibility,
        onItemUnchecked = viewModel::onItemUnchecked,
        onItemChecked = viewModel::onItemChecked,
        onItemTextChanged = viewModel::onItemTextChanged,
        onDoneClicked = viewModel::onDoneClicked,
        onDeleteClick = viewModel::onDeleteClick,
        onMoveItems = viewModel::onMoveItems,
        onTitleNextClick = viewModel::onTitleNextClick,
        onMoveCompleted = viewModel::onMoveCompleted,
        onItemFocused = viewModel::onItemFocused,
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
    onDeleteClick: (UncheckedListItemUi) -> Unit,
    onMoveItems: (fromIndex: Int, toIndex: Int) -> Unit,
    onTitleNextClick: () -> Unit,
    onMoveCompleted: () -> Unit,
    onItemFocused: (UncheckedListItemUi) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        val transitionKey = remember(state.checklistId) { state.asTransitionKey(elementName = "card") }
        Column(
            modifier = Modifier
                .sharedBoundsTransition(transitionKey = transitionKey)
                .fillMaxSize()
        ) {
            EditActionBar(
                pinTransitionKey = remember(state.checklistId) { state.asTransitionKey(elementName = "pin") },
                systemBarInset = innerPadding.calculateTopPadding(),
                pinned = state.isPinned,
                onBackClick = onBackClick,
                onPinCheckedChange = onPinCheckedChange
            )
            Box {
                val paddingBottom = remember(innerPadding) { innerPadding.calculateBottomPadding() + 40.dp }
                ChecklistBody(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPaddingBottom = paddingBottom,
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
                    onDeleteClick = onDeleteClick,
                    onMoveItems = onMoveItems,
                    onTitleNextClick = onTitleNextClick,
                    onMoveCompleted = onMoveCompleted,
                    onItemFocused = onItemFocused,
                )
                ModificationDateOverlay(
                    navigationBarPadding = innerPadding.calculateBottomPadding(),
                    message = state.modificationStatusMessage,
                )
            }
        }
    }
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
            onDeleteClick = {},
            onMoveItems = { _, _ -> },
            onTitleNextClick = {},
            onMoveCompleted = {},
            onItemFocused = {},
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