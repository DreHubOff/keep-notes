package com.jksol.keep.notes.ui.screens.trash.listitem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.shared.listitem.MainItemContainer

private const val MAX_LINES_TITLE = 5
private const val MAX_LINES_CONTENT = 10

@Composable
fun TrashTextNote(
    modifier: Modifier,
    item: MainScreenItem.TextNote,
    onClick: (() -> Unit)? = null,
) {
    MainItemContainer(
        modifier = modifier,
        item = item,
        maxTitleLines = MAX_LINES_TITLE,
        onClick = onClick,
        content = { contentModifier ->
            if (item.content.isNotEmpty()) {
                ContentText(modifier = contentModifier, textItem = item)
            }
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