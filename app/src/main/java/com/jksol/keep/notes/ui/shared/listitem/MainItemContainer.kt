@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.shared.listitem

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.sharedBoundsTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedCardBorder
import com.jksol.keep.notes.ui.theme.themedCardColors

@Composable
fun MainItemContainer(
    modifier: Modifier = Modifier,
    cardTransitionKey: Any,
    title: String,
    customBackground: Color? = null,
    isSelected: Boolean = false,
    maxTitleLines: Int = 5,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    itemStatus: (@Composable RowScope.() -> Unit)?,
    content: @Composable (Modifier) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val cardColors = key(isSelected, customBackground) {
        val base = themedCardColors(isSelected = isSelected)
        if (isSelected || customBackground == null) base else base.copy(containerColor = customBackground)
    }
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .sharedBoundsTransition(transitionKey = cardTransitionKey),
        colors = cardColors,
        border = themedCardBorder(isSelected = isSelected),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (title.isNotEmpty()) {
                WithTitleNote(
                    title = title,
                    itemStatus = itemStatus,
                    content = content,
                    maxTitleLines = maxTitleLines,
                )
            } else {
                WithoutTitleNote(
                    itemStatus = itemStatus,
                    content = content,
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        enabled = onClick != null,
                        onClick = { onClick?.invoke() },
                        onLongClick = onLongClick?.let {
                            {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongClick()
                            }
                        },
                    )
            ) {
            }
        }
    }
}

@Composable
private fun WithTitleNote(
    title: String,
    maxTitleLines: Int,
    content: @Composable (Modifier) -> Unit,
    itemStatus: @Composable() (RowScope.() -> Unit)?,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = spacedBy(10.dp)) {
            TitleText(
                modifier = Modifier.weight(1f),
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
    ApplicationTheme {
        MainItemContainer(
            cardTransitionKey = Unit,
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
    ApplicationTheme {
        MainItemContainer(
            cardTransitionKey = "preview_card_icon",
            title = "Trashed Reminder",
            maxTitleLines = 1,
            isSelected = true,
            onClick = {},
            itemStatus = {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
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

@Preview(showBackground = true)
@Composable
private fun WithIconStatusCustomBackgroundPreview() {
    ApplicationTheme {
        MainItemContainer(
            cardTransitionKey = "preview_card_icon",
            title = "Trashed Reminder",
            maxTitleLines = 1,
            isSelected = false,
            customBackground = Color.Magenta,
            onClick = {},
            itemStatus = {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
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
