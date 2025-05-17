package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Alarm
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R

@Composable
fun MainScreenItemContainer(
    modifier: Modifier,
    item: MainScreenItem,
    maxTitleLines: Int = 2,
    content: @Composable (Modifier) -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        if (item.title.isNotEmpty()) {
            WithTitleNote(
                textItem = item,
                content = content,
                maxTitleLines = maxTitleLines,
            )
        } else {
            WithoutTitleNote(
                textItem = item,
                content = content,
            )
        }
    }
}

@Composable
private fun WithTitleNote(
    textItem: MainScreenItem,
    maxTitleLines: Int = 2,
    content: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = spacedBy(10.dp)) {
            TitleText(modifier = Modifier.weight(1f), title = textItem.title, mixLines = maxTitleLines)
            StatusIcons(textItem)
        }
        content(Modifier)
    }
}

@Composable
private fun WithoutTitleNote(
    textItem: MainScreenItem,
    content: @Composable (Modifier) -> Unit,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = spacedBy(10.dp)
    ) {
        content(Modifier.weight(1f))
        StatusIcons(textItem)
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

@Composable
private fun StatusIcons(textItem: MainScreenItem) {
    Row(horizontalArrangement = spacedBy(10.dp)) {
        if (textItem.hasScheduledReminder) {
            Icon(
                imageVector = Icons.Sharp.Alarm,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        if (textItem.isPinned) {
            Icon(
                painter = painterResource(R.drawable.ic_material_keep),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
