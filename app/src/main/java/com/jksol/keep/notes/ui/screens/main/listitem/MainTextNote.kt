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
import com.jksol.keep.notes.ui.shared.listitem.TextNoteCard
import com.jksol.keep.notes.ui.shared.listitem.TextNoteCardData
import com.jksol.keep.notes.ui.shared.rememberTextNotePinToEditorTransitionKey
import com.jksol.keep.notes.ui.shared.rememberTextNoteToEditorTransitionKey
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainTextNote(
    modifier: Modifier,
    item: MainScreenItem.TextNote,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val cardTransitionKey = rememberTextNoteToEditorTransitionKey(noteId = item.id)
    val cardData = remember(item, cardTransitionKey) {
        TextNoteCardData(
            transitionKey = cardTransitionKey,
            title = item.title,
            content = item.content,
            isSelected = item.isSelected,
        )
    }
    TextNoteCard(
        modifier = modifier,
        item = cardData,
        onClick = if (item.interactive) onClick else null,
        onLongClick = if (item.interactive) onLongClick else null,
        itemStatus = {
            MainItemStatusIcons(
                isPinned = item.isPinned,
                pinTransitionKey = rememberTextNotePinToEditorTransitionKey(noteId = item.id),
                hasScheduledReminder = item.hasScheduledReminder,
            )
        }
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MainTextNoteStateProvider::class) state: MainScreenItem.TextNote) {
    ApplicationTheme {
        MainTextNote(Modifier.padding(8.dp), state)
    }
}

private class MainTextNoteStateProvider : PreviewParameterProvider<MainScreenItem.TextNote> {
    override val values: Sequence<MainScreenItem.TextNote>
        get() = sequenceOf(
            MainScreenDemoData.TextNotes.welcomeBanner,
            MainScreenDemoData.TextNotes.reminderPinnedNote,
            MainScreenDemoData.TextNotes.emptyTitleNote,
            MainScreenDemoData.TextNotes.reminderPinnedNoteEmptyTitle,
            MainScreenDemoData.TextNotes.reminderPinnedNoteLongTitle,
            MainScreenDemoData.TextNotes.reminderPinnedNoteLongTitle,
            MainScreenDemoData.TextNotes.pinnedOnlyNote,
            MainScreenDemoData.TextNotes.reminderOnlyNote,
            MainScreenDemoData.TextNotes.emptyContentNote,
        )
}