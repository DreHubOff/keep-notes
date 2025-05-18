package com.jksol.keep.notes.ui.screens.main.listitem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.theme.ApplicationTheme

private const val MAX_LINES_TITLE = 2
private const val MAX_LINES_CONTENT = 5

@Composable
fun MainTextNote(
    modifier: Modifier,
    item: MainScreenItem.TextNote,
    onClick: (() -> Unit)? = null,
) {
    MainScreenItemContainer(
        modifier = modifier,
        item = item,
        maxTitleLines = MAX_LINES_TITLE,
        onClick = onClick,
        content = { contentModifier ->
            ContentText(modifier = contentModifier, textItem = item)
        }
    )
}

@Composable
private fun ContentText(modifier: Modifier, textItem: MainScreenItem.TextNote) {
    Text(
        modifier = modifier,
        text = textItem.content,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = MAX_LINES_CONTENT,
        overflow = TextOverflow.Ellipsis,
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
        )
}