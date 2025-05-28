package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ReorderableCollectionItemScope.DraggableChecklistItem(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean = true,
    focusRequest: ElementFocusRequest? = null,
    onCheckedChange: (Boolean) -> Unit = {},
    onTextChanged: (String) -> Unit = {},
    onDoneClicked: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onDragCompleted: () -> Unit = {},
    onItemFocused: () -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    val itemElevation by animateDpAsState(
        targetValue = if (isDragging) 4.dp else 0.dp,
        label = "draggableItemElevation"
    )
    Surface(
        shadowElevation = itemElevation,
        color = if (itemElevation.value != 0f) MaterialTheme.colorScheme.surface else Color.Transparent,
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(8.dp),
        ) {
            val haptic = LocalHapticFeedback.current
            Icon(
                modifier = Modifier
                    .draggableHandle(
                        onDragStarted = {
                            isDragging = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragStopped = {
                            isDragging = false
                            onDragCompleted()
                        }
                    ),
                imageVector = Icons.Sharp.DragIndicator,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(R.string.drag_current_item)
            )
            EditableChecklistCheckbox(
                modifier = Modifier,
                text = title,
                checked = checked,
                isDragging = isDragging,
                focusRequest = focusRequest,
                onCheckedChange = onCheckedChange,
                onTextChanged = onTextChanged,
                onDoneClicked = onDoneClicked,
                onDeleteClick = onDeleteClick,
                onItemFocused = onItemFocused,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        val lazyListState = rememberLazyListState()
        val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
            // Update the list
        }
        LazyColumn(state = lazyListState) {
            item {
                ReorderableItem(reorderableLazyListState, 1) {
                    DraggableChecklistItem(
                        title = "This is a title",
                        checked = false,
                        onItemFocused = { },
                    )
                }
            }
        }
    }
}