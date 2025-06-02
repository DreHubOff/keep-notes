package com.jksol.keep.notes.ui.screens.main.selection

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.main.listitem.MainChecklist
import com.jksol.keep.notes.ui.screens.main.listitem.MainTextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun MainScreenStateSelection(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    listItems: List<MainScreenItem>,
    selectedItemCount: Int,
    onExitSelectionMode: () -> Unit,
    onMoveToTrashClick: () -> Unit,
    onPinnedStateChanged: (Boolean) -> Unit,
    selectTextNote: (MainScreenItem.TextNote) -> Unit,
    selectChecklist: (MainScreenItem.Checklist) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        SelectionActionBar(
            innerPadding = innerPadding,
            onExitSelectionMode = {
                coroutineScope.launch {
                    if (listItems.isNotEmpty()) {
                        scrollState.animateScrollToItem(0)
                    }
                    onExitSelectionMode()
                }
            },
            selectedItemCount = selectedItemCount,
            onMoveToTrashClick = onMoveToTrashClick,
            onPinnedStateChanged = onPinnedStateChanged,
        )
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp, top = 16.dp),
            verticalArrangement = spacedBy(8.dp),
            state = scrollState,
        ) {
            items(listItems, key = { it.compositeKey }) { item ->
                when (item) {
                    is MainScreenItem.Checklist ->
                        MainChecklist(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            item = item,
                            onClick = { selectChecklist(item) },
                        )

                    is MainScreenItem.TextNote ->
                        MainTextNote(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            item = item,
                            onClick = { selectTextNote(item) },
                        )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainScreenStateSelection(
            innerPadding = PaddingValues(20.dp),
            listItems = MainScreenDemoData.notesList(),
            selectTextNote = {},
            selectChecklist = {},
            onExitSelectionMode = {},
            onMoveToTrashClick = {},
            onPinnedStateChanged = {},
            selectedItemCount = 123,
        )
    }
}