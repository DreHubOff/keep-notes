package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

private const val MAX_LINES_TITLE = 2

@Composable
fun MainCheckList(modifier: Modifier, item: MainScreenItem.CheckList) {
    MainScreenItemContainer(
        modifier = modifier,
        item = item,
        maxTitleLines = MAX_LINES_TITLE
    ) { contentModifier ->
        ChecklistContent(modifier = contentModifier, item = item)
    }
}

@Composable
private fun ChecklistContent(modifier: Modifier, item: MainScreenItem.CheckList) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ChecklistItems(item)
        if (item.isOverfilled) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.ticked_items_counter).format(item.tickedItems),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ChecklistItems(item: MainScreenItem.CheckList) {
    Column(
        modifier = Modifier,
    ) {
        item.items.forEach { checkListItem ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    modifier = Modifier.height(34.dp),
                    checked = checkListItem.isChecked,
                    onCheckedChange = {

                    },
                    enabled = false,
                )
                Text(
                    text = checkListItem.text,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MainCheckListStateProvider::class) state: MainScreenItem.CheckList) {
    ApplicationTheme {
        MainCheckList(modifier = Modifier.padding(8.dp), item = state)
    }
}

private class MainCheckListStateProvider : PreviewParameterProvider<MainScreenItem.CheckList> {
    override val values: Sequence<MainScreenItem.CheckList>
        get() = sequenceOf(
            MainScreenDemoData.CheckLists.reminderPinnedChecklist,
            MainScreenDemoData.CheckLists.reminderOnlyChecklist,
            MainScreenDemoData.CheckLists.pinnedOnlyChecklist,
            MainScreenDemoData.CheckLists.emptyTitleChecklist,
            MainScreenDemoData.CheckLists.longTitleChecklist,
        )
}