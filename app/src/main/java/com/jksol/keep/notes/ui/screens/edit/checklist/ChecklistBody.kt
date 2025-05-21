package com.jksol.keep.notes.ui.screens.edit.checklist

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.EditChecklistDemoData
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import sh.calvin.reorderable.ReorderableColumn

@Composable
fun ChecklistBody(
    modifier: Modifier,
    title: String,
    checkedItems: List<CheckedListItemUi> = emptyList(),
    uncheckedItems: List<UncheckedListItemUi> = emptyList(),
    showCheckedItems: Boolean = false,
    onTitleChanged: (String) -> Unit = {},
    onAddChecklistItemClick: () -> Unit = {},
    toggleCheckedItemsVisibility: () -> Unit = {},
    onItemUnchecked: (CheckedListItemUi) -> Unit = {},
    onItemChecked: (UncheckedListItemUi) -> Unit = {},
    onItemTextChanged: (String, UncheckedListItemUi) -> Unit = { _, _ -> },
    onDoneClicked: (UncheckedListItemUi) -> Unit = {},
    onFocusStateChanged: (Boolean, UncheckedListItemUi) -> Unit = { _, _ -> },
    onDeleteClick: (UncheckedListItemUi) -> Unit = {},
) {
    var titleCache by remember(title) { mutableStateOf(title) }
    Column(
        modifier = modifier,
    ) {
        Title(
            title = titleCache,
            onTitleChanged = {
                titleCache = it
                onTitleChanged(it)
            },
            onNextClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uncheckedItems.isNotEmpty()) {
            UncheckedItems(
                items = uncheckedItems,
                onItemChecked = onItemChecked,
                onTextChanged = onItemTextChanged,
                onDoneClicked = onDoneClicked,
                onFocusStateChanged = onFocusStateChanged,
                onDeleteClick = onDeleteClick,
            )
        }

        AddItemButton(onAddClick = onAddChecklistItemClick)

        if (checkedItems.isNotEmpty()) {
            HideCheckedItemsButton(
                checked = showCheckedItems.not(),
                hiddenItemCount = checkedItems.size,
                toggleCheckedItemsVisibility = toggleCheckedItemsVisibility,
            )
            if (showCheckedItems) {
                CheckedItems(
                    checkedItems = checkedItems,
                    onItemUnchecked = onItemUnchecked,
                )
            }
        }
    }
}

@Composable
private fun CheckedItems(
    checkedItems: List<CheckedListItemUi>,
    onItemUnchecked: (CheckedListItemUi) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 24.dp),
        verticalArrangement = spacedBy(4.dp),
    ) {
        checkedItems.forEach { item ->
            key(item.id) {
                EditableChecklistCheckbox(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.text,
                    checked = true,
                    onCheckedChange = { onItemUnchecked(item) },
                )
            }
        }
    }
}

@Composable
private fun HideCheckedItemsButton(
    checked: Boolean,
    hiddenItemCount: Int,
    toggleCheckedItemsVisibility: () -> Unit,
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = -14f
            },
        onClick = toggleCheckedItemsVisibility,
        contentPadding = PaddingValues(horizontal = 4.dp),
        border = null,
    ) {
        val context = LocalContext.current
        val text = remember(hiddenItemCount) {
            context
                .getString(R.string.checked_item_count_pattern)
                .format(hiddenItemCount)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(6.dp),
        ) {
            val iconRotation by animateFloatAsState(
                targetValue = if (checked) 180f else 0f,
            )
            Icon(
                modifier = Modifier
                    .graphicsLayer { rotationZ = iconRotation }
                    .size(22.dp),
                imageVector = Icons.Sharp.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun AddItemButton(onAddClick: () -> Unit) {
    TextButton(
        modifier = Modifier,
        onClick = onAddClick,
        contentPadding = PaddingValues(start = 36.dp, end = 36.dp),
        border = null,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                imageVector = Icons.Sharp.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.add_item),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun UncheckedItems(
    items: List<UncheckedListItemUi>,
    onItemChecked: (UncheckedListItemUi) -> Unit,
    onTextChanged: (String, UncheckedListItemUi) -> Unit,
    onDoneClicked: (UncheckedListItemUi) -> Unit,
    onFocusStateChanged: (Boolean, UncheckedListItemUi) -> Unit,
    onDeleteClick: (UncheckedListItemUi) -> Unit,
) {
    ReorderableColumn(
        list = items,
        onSettle = { fromIndex, toIndex ->
            Log.d("ChecklistBody", "onSettle: $fromIndex -> $toIndex")
        },
        verticalArrangement = spacedBy(4.dp),
    ) { _, item, _ ->
        key(item.id) {
            DraggableChecklistItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .draggableHandle(),
                title = item.text,
                checked = false,
                isFocused = item.isFocused,
                onCheckedChange = { onItemChecked(item) },
                onTextChanged = { onTextChanged(it, item) },
                onDoneClicked = { onDoneClicked(item) },
                onFocusStateChanged = { onFocusStateChanged(it, item) },
                onDeleteClick = { onDeleteClick(item) },
            )
        }
    }
}

@Composable
private fun Title(
    title: String,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit = {},
    onNextClick: () -> Unit = {},
) {
    BasicTextField(
        value = title,
        onValueChange = onTitleChanged,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onNextClick() }),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxWidth()) {
                if (title.isEmpty()) {
                    Text(
                        text = stringResource(R.string.title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        ChecklistBody(
            modifier = Modifier.fillMaxWidth(),
            title = "This is a title",
        )
    }
}

@Preview(name = "PreviewList", showBackground = true)
@Composable
private fun PreviewList() {
    ApplicationTheme {
        ChecklistBody(
            modifier = Modifier.fillMaxWidth(),
            title = "This is a title",
            uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
            checkedItems = EditChecklistDemoData.checkedChecklistItems,
            showCheckedItems = true,
        )
    }
}