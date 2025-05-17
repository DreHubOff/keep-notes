package com.jksol.keep.notes.ui.screens.main

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.ui.theme.ApplicationTheme

private const val MAX_LINES_TITLE = 2
private const val MAX_LINES_CONTENT = 5

@Composable
fun MainTextNote(textItem: MainScreenItem.TextNote) {
    MainScreenItemContainer(item = textItem, maxTitleLines = MAX_LINES_TITLE) { modifier ->
        ContentText(modifier = modifier, textItem = textItem)
    }
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
        MainTextNote(state)
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