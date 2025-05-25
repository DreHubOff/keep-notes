@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.shared.listitem

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.ui.shared.sharedBoundsTransition
import com.jksol.keep.notes.ui.shared.sharedElementTransition

@Composable
fun MainItemContainer(
    modifier: Modifier = Modifier,
    cardTransitionKey: Any,
    titleTransitionKey: Any,
    contentTransitionKey: Any,
    title: String,
    maxTitleLines: Int,
    onClick: (() -> Unit)?,
    itemStatus: (@Composable RowScope.() -> Unit)?,
    content: @Composable (Modifier) -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .sharedBoundsTransition(transitionKey = cardTransitionKey),
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
    ) {
        val contentModifier = Modifier.sharedElementTransition(transitionKey = contentTransitionKey)
        Box {
            if (title.isNotEmpty()) {
                WithTitleNote(
                    transitionKey = titleTransitionKey,
                    title = title,
                    itemStatus = itemStatus,
                    content = { content(it.then(contentModifier)) },
                    maxTitleLines = maxTitleLines,
                )
            } else {
                WithoutTitleNote(
                    itemStatus = itemStatus,
                    content = { content(it.then(contentModifier)) },
                )
            }
            Box(modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    enabled = onClick != null
                ) {
                    onClick?.invoke()
                }) {
            }
        }
    }
}

@Composable
private fun WithTitleNote(
    title: String,
    maxTitleLines: Int,
    transitionKey: Any,
    content: @Composable (Modifier) -> Unit,
    itemStatus: @Composable() (RowScope.() -> Unit)?,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = spacedBy(10.dp)) {
            TitleText(
                modifier = Modifier
                    .weight(1f)
                    .sharedElementTransition(transitionKey = transitionKey),
                title = title,
                mixLines = maxTitleLines
            )
            if (itemStatus != null) {
                Row(
                    horizontalArrangement = spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    itemStatus()
                }
            }
        }
        content(Modifier)
    }
}

@Composable
private fun WithoutTitleNote(
    content: @Composable (Modifier) -> Unit,
    itemStatus: @Composable() (RowScope.() -> Unit)?,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = spacedBy(10.dp)
    ) {
        content(Modifier.weight(1f))
        if (itemStatus != null) {
            Row(
                horizontalArrangement = spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemStatus()
            }
        }
    }
}

@Composable
private fun TitleText(modifier: Modifier, title: String, mixLines: Int) {
    Text(
        modifier = modifier,
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.W600,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = mixLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithStatusText() {
    MaterialTheme {
        MainItemContainer(
            cardTransitionKey = Unit,
            titleTransitionKey = Unit,
            contentTransitionKey = Unit,
            title = "Deleted Note",
            maxTitleLines = 1,
            onClick = {},
            itemStatus = {
                Text(
                    text = "Deleted",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            },
            content = { modifier ->
                Text(
                    modifier = modifier,
                    text = "This is a preview of a trashed note's content.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WithIconStatusPreview() {
    MaterialTheme {
        MainItemContainer(
            cardTransitionKey = "preview_card_icon",
            titleTransitionKey = "preview_title_icon",
            contentTransitionKey = "preview_content_icon",
            title = "Trashed Reminder",
            maxTitleLines = 1,
            onClick = {},
            itemStatus = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Deleted icon",
                    tint = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = "Deleted",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            },
            content = { modifier ->
                Text(
                    modifier = modifier,
                    text = "Here’s what was in the deleted item.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}
