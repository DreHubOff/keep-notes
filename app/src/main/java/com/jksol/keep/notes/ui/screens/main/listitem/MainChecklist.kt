package com.jksol.keep.notes.ui.screens.main.listitem

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.shared.listitem.ChecklistCard
import com.jksol.keep.notes.ui.shared.listitem.ChecklistCardData
import com.jksol.keep.notes.ui.shared.rememberChecklistToEditorPinTransitionKey
import com.jksol.keep.notes.ui.shared.rememberChecklistToEditorTransitionKey
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainChecklist(
    modifier: Modifier,
    item: MainScreenItem.Checklist,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {
    val cardTransitionKey = rememberChecklistToEditorTransitionKey(checklistId = item.id)
    val rememberChecklistCardData = remember(item, cardTransitionKey) {
        ChecklistCardData(
            transitionKey = cardTransitionKey,
            title = item.title,
            items = item.items.map { it.text },
            tickedItemsCount = item.tickedItems,
            isSelected = item.isSelected,
        )
    }
    ChecklistCard(
        modifier = modifier,
        item = rememberChecklistCardData,
        onClick = if (item.interactive) onClick else null,
        onLongClick = if (item.interactive) onLongClick else null,
        itemStatus = {
            MainItemStatusIcons(
                isPinned = item.isPinned,
                pinTransitionKey = rememberChecklistToEditorPinTransitionKey(checklistId = item.id),
                hasScheduledReminder = item.hasScheduledReminder,
            )
        }
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MainCheckListStateProvider::class) state: MainScreenItem.Checklist) {
    ApplicationTheme {
        MainChecklist(modifier = Modifier.padding(8.dp), item = state)
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