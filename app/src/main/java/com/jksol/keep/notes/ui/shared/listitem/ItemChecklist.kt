package com.jksol.keep.notes.ui.shared.listitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.ChecklistCheckbox

@Composable
fun ChecklistCard(
    modifier: Modifier,
    item: ChecklistCardData,
    onClick: (() -> Unit)? = {},
    itemStatus: (@Composable RowScope.() -> Unit)?,
) {
    MainItemContainer(
        modifier = modifier,
        cardTransitionKey = item.transitionKey,
        title = item.title,
        onClick = onClick,
        itemStatus = itemStatus,
    ) { contentModifier ->
        if (item.items.isNotEmpty() || item.tickedItemsCount > 0) {
            ChecklistContent(
                modifier = contentModifier,
                tickedItemsCount = item.tickedItemsCount,
                visibleItems = item.items,
            )
        }
    }
}

@Composable
private fun ChecklistContent(
    modifier: Modifier,
    tickedItemsCount: Int,
    visibleItems: List<String>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ChecklistItems(items = visibleItems)
        if (tickedItemsCount > 0) {
            Text(
                modifier = Modifier.padding(horizontal = 6.dp),
                text = stringResource(R.string.ticked_items_counter).format(tickedItemsCount),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ChecklistItems(items: List<String>) {
    Column(
        modifier = Modifier,
    ) {
        items.forEach { checklistItem ->
            ChecklistCheckbox(
                modifier = Modifier.fillMaxWidth(),
                text = checklistItem,
                checked = false,
                enabled = false,
            )
        }
    }
}