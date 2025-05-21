package com.jksol.keep.notes.ui.screens.main.listitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.shared.ChecklistCheckbox
import com.jksol.keep.notes.ui.theme.ApplicationTheme

private const val MAX_LINES_TITLE = 5

@Composable
fun MainCheckList(
    modifier: Modifier,
    item: MainScreenItem.Checklist,
    onClick: () -> Unit = {},
) {
    MainScreenItemContainer(
        modifier = modifier,
        item = item,
        maxTitleLines = MAX_LINES_TITLE,
        onClick = onClick,
    ) { contentModifier ->
        if (item.items.isNotEmpty()) {
            ChecklistContent(modifier = contentModifier, item = item, onItemClicked = onClick)
        }
    }
}

@Composable
private fun ChecklistContent(
    modifier: Modifier,
    item: MainScreenItem.Checklist,
    onItemClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ChecklistItems(item, onItemClicked)
        if (item.hasTickedItems) {
            Text(
                modifier = Modifier.padding(horizontal = 6.dp),
                text = stringResource(R.string.ticked_items_counter).format(item.tickedItems),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ChecklistItems(
    item: MainScreenItem.Checklist,
    onItemClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier,
    ) {
        item.items.forEach { checkListItem ->
            ChecklistCheckbox(
                modifier = Modifier.fillMaxWidth(),
                text = checkListItem.text,
                checked = checkListItem.isChecked,
                enabled = true,
                onClick = onItemClicked,
            )
        }
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MainCheckListStateProvider::class) state: MainScreenItem.Checklist) {
    ApplicationTheme {
        MainCheckList(modifier = Modifier.padding(8.dp), item = state)
    }
}

private class MainCheckListStateProvider : PreviewParameterProvider<MainScreenItem.Checklist> {
    override val values: Sequence<MainScreenItem.Checklist>
        get() = sequenceOf(
            MainScreenDemoData.CheckLists.reminderPinnedChecklist,
            MainScreenDemoData.CheckLists.reminderOnlyChecklist,
            MainScreenDemoData.CheckLists.pinnedOnlyChecklist,
            MainScreenDemoData.CheckLists.emptyTitleChecklist,
            MainScreenDemoData.CheckLists.emptyContentChecklist,
            MainScreenDemoData.CheckLists.longTitleChecklist,
        )
}