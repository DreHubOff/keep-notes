@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.trash

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.ui.screens.trash.listitem.TrashChecklist
import com.jksol.keep.notes.ui.screens.trash.listitem.TrashTextNote
import com.jksol.keep.notes.ui.screens.trash.model.TrashListItem
import com.jksol.keep.notes.ui.screens.trash.model.TrashScreenState
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun TrashScreen() {
//    val viewModel: EditNoteViewModel = hiltViewModel()
//    val state by viewModel.state.collectAsState(EditNoteScreenState.None)
//
//    BackHandler {
//        viewModel.onBackClicked()
//    }
//
//    if (state !is EditNoteScreenState.None) {
//        ScreenContent(
//            state = state as EditNoteScreenState.Idle,
//            onTitleChanged = viewModel::onTitleChanged,
//            onContentChanged = viewModel::onContentChanged,
//            onBackClick = viewModel::onBackClicked,
//            onPinCheckedChange = viewModel::onPinCheckedChange,
//            onDeleteClick = viewModel::moveToTrash,
//        )
//    }
}

@Composable
private fun ScreenContent(
    state: TrashScreenState,
    onBackClick: () -> Unit = {},
    onItemClick: (TrashListItem) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {

        }
    ) { innerPadding ->
        Column {
            TrashActionBar(
                systemBarInset = innerPadding.calculateTopPadding(),
                onBackClick = onBackClick,
                showMenu = state.listItems.isNotEmpty()
            )
            DisplayState(
                state = state,
                innerPadding = innerPadding,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
private fun DisplayState(
    state: TrashScreenState,
    onItemClick: (TrashListItem) -> Unit,
    innerPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = spacedBy(8.dp),
    ) {
        items(items = state.listItems, key = { it.compositeKey }) { item ->
            when (item) {
                is TrashListItem.Checklist -> {
                    TrashChecklist(
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 8.dp),
                        item = item,
                        onClick = { onItemClick(item) },
                    )
                }

                is TrashListItem.TextNote -> {
                    TrashTextNote(
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 8.dp),
                        item = item,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditNoteScreenStateProvider::class) state: TrashScreenState) {
    ApplicationTheme {
        ScreenContent(state = state)
    }
}

private class EditNoteScreenStateProvider : PreviewParameterProvider<TrashScreenState> {
    override val values: Sequence<TrashScreenState>
        get() = sequenceOf(
            // Empty list
            TrashScreenState(listItems = emptyList()),

            // One text note
            TrashScreenState(
                listItems = listOf(
                    TrashListItem.TextNote(
                        id = 1L,
                        title = "Deleted Note",
                        content = "This is a deleted text note.",
                        daysLeftMessage = "6 days left"
                    )
                )
            ),

            // One checklist
            TrashScreenState(
                listItems = listOf(
                    TrashListItem.Checklist(
                        id = 2L,
                        title = "Groceries",
                        items = listOf("Milk", "Eggs", "Bread"),
                        tickedItems = 1,
                        daysLeftMessage = "2 days left"
                    )
                )
            ),

            // Mixed short list
            TrashScreenState(
                listItems = listOf(
                    TrashListItem.TextNote(
                        id = 3L,
                        title = "Project ideas",
                        content = "Build a Compose library...",
                        daysLeftMessage = "4 days left"
                    ),
                    TrashListItem.Checklist(
                        id = 4L,
                        title = "Travel Checklist",
                        items = listOf("Passport", "Charger", "Sunglasses"),
                        tickedItems = 2,
                        daysLeftMessage = "5 days left"
                    )
                )
            ),

            // Longer mixed list
            TrashScreenState(
                listItems = listOf(
                    TrashListItem.TextNote(
                        id = 5L,
                        title = "Meeting Notes",
                        content = "Discuss release timeline...",
                        daysLeftMessage = "1 day left"
                    ),
                    TrashListItem.Checklist(
                        id = 6L,
                        title = "Packing List",
                        items = listOf("Shoes", "T-Shirts", "Toothbrush"),
                        tickedItems = 3,
                        daysLeftMessage = "3 days left"
                    ),
                    TrashListItem.TextNote(
                        id = 7L,
                        title = "Poem Draft",
                        content = "Roses are red...",
                        daysLeftMessage = "7 days left"
                    ),
                    TrashListItem.Checklist(
                        id = 8L,
                        title = "Daily Routine",
                        items = listOf("Workout", "Read", "Code"),
                        tickedItems = 0,
                        daysLeftMessage = "6 days left"
                    ),
                    TrashListItem.TextNote(
                        id = 9L,
                        title = "Meeting Notes",
                        content = "Discuss release timeline...",
                        daysLeftMessage = "1 day left"
                    ),
                    TrashListItem.Checklist(
                        id = 10L,
                        title = "Packing List",
                        items = listOf("Shoes", "T-Shirts", "Toothbrush"),
                        tickedItems = 3,
                        daysLeftMessage = "3 days left"
                    ),
                    TrashListItem.TextNote(
                        id = 11L,
                        title = "Poem Draft",
                        content = "Roses are red...",
                        daysLeftMessage = "7 days left"
                    ),
                    TrashListItem.Checklist(
                        id = 12L,
                        title = "Daily Routine",
                        items = listOf("Workout", "Read", "Code"),
                        tickedItems = 0,
                        daysLeftMessage = "6 days left"
                    )
                )
            )
        )
}