package com.jksol.keep.notes.ui.screens.main

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.shared.listitem.ItemChecklist
import com.jksol.keep.notes.ui.screens.trash.listitem.TrashTextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.search.MainSearchBarEntryPoint
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun MainScreenStateIdle(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    listItems: List<MainScreenItem>,
    onToggleSearchVisibility: () -> Unit = {},
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit = {},
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit = {},
    onOpenMenuClick: () -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    if (listItems.isEmpty()) {
        Column {
            MainSearchBarEntryPoint(
                innerPadding = innerPadding,
                onSearchClick = onToggleSearchVisibility,
                onOpenMenuClick = onOpenMenuClick,
            )
            MainScreenEmptyList(
                modifier = Modifier
                    .padding(bottom = 100.dp)
                    .fillMaxSize(),
                message = stringResource(R.string.notes_you_add_appear_here),
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = spacedBy(8.dp),
        state = scrollState,
    ) {
        item {
            MainSearchBarEntryPoint(
                innerPadding = innerPadding,
                modifier = Modifier.padding(bottom = 8.dp),
                onSearchClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                        onToggleSearchVisibility()
                    }
                },
                onOpenMenuClick = onOpenMenuClick,
            )
        }

        items(listItems, key = { it.compositeKey }) { item ->
            when (item) {
                is MainScreenItem.Checklist ->
                    ItemChecklist(
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 8.dp),
                        item = item,
                        onClick = { openCheckListEditor(item) }
                    )

                is MainScreenItem.TextNote ->
                    TrashTextNote(
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 8.dp),
                        item = item,
                        onClick = { openTextNoteEditor(item) }
                    )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainScreenStateIdle(
            innerPadding = PaddingValues(20.dp),
            listItems = MainScreenDemoData.notesList(),
            openTextNoteEditor = {},
            openCheckListEditor = {},
        )
    }
}