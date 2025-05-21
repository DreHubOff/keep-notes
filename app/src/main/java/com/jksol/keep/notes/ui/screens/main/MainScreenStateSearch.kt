package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.main.listitem.MainCheckList
import com.jksol.keep.notes.ui.screens.main.listitem.MainTextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.search.MainSearchBar
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun MainScreenStateSearch(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    listItems: List<MainScreenItem>,
    onHideSearch: () -> Unit = {},
    onNewPrompt: (String) -> Unit = {},
    openTextNoteEditor: (MainScreenItem.TextNote?) -> Unit,
    openCheckListEditor: (MainScreenItem.Checklist?) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        MainSearchBar(
            innerPadding = innerPadding,
            onHideSearch = {
                coroutineScope.launch {
                    if (listItems.isNotEmpty()) {
                        scrollState.animateScrollToItem(0)
                    }
                    onHideSearch()
                }
            },
            onValueChanged = onNewPrompt,
        )
        if (listItems.isEmpty()) {
            MainScreenEmptyList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp)
                    .weight(1f),
                message = stringResource(R.string.search_empty_list)
            )
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, top = 16.dp),
                verticalArrangement = spacedBy(8.dp),
                state = scrollState,
            ) {
                items(listItems, key = { it.compositeKey }) { item ->
                    when (item) {
                        is MainScreenItem.Checklist ->
                            MainCheckList(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                item = item,
                                onClick = { openCheckListEditor(item) }
                            )

                        is MainScreenItem.TextNote ->
                            MainTextNote(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                item = item,
                                onClick = { openTextNoteEditor(item) }
                            )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainScreenStateSearch(
            innerPadding = PaddingValues(20.dp),
            listItems = MainScreenDemoData.notesList(),
            onHideSearch = {},
            onNewPrompt = {},
            openTextNoteEditor = {},
            openCheckListEditor = {},
        )
    }
}