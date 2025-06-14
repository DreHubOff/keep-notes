@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.LocalThemeMode
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ThemeMode
import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.demo_data.EditChecklistDemoData
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.core.ReminderButton
import com.jksol.keep.notes.ui.screens.edit.core.ReminderStateData
import com.jksol.keep.notes.ui.shared.sharedElementTransition
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.OffsetDateTime

@Composable
fun ChecklistBody(
    modifier: Modifier,
    title: String,
    contentPaddingBottom: Dp = 0.dp,
    checkedItems: List<CheckedListItemUi> = emptyList(),
    uncheckedItems: List<UncheckedListItemUi> = emptyList(),
    focusRequests: List<ElementFocusRequest> = emptyList(),
    showCheckedItems: Boolean = false,
    reminderStateData: ReminderStateData? = null,
    titleTransitionKey: Any = Unit,
    backgroundColor: NoteColor? = null,
    onTitleChanged: (String) -> Unit = {},
    onTitleNextClick: () -> Unit = {},
    onAddChecklistItemClick: () -> Unit = {},
    toggleCheckedItemsVisibility: () -> Unit = {},
    onItemUnchecked: (CheckedListItemUi) -> Unit = {},
    onItemChecked: (UncheckedListItemUi) -> Unit = {},
    onItemTextChanged: (String, UncheckedListItemUi) -> Unit = { _, _ -> },
    onDoneClicked: (UncheckedListItemUi) -> Unit = {},
    onDeleteClick: (UncheckedListItemUi) -> Unit = {},
    onMoveItems: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
    onMoveCompleted: () -> Unit = { },
    onItemFocused: (UncheckedListItemUi) -> Unit = {},
    onEditReminderClick: () -> Unit = {},
) {
    var titleCache by remember(title) { mutableStateOf(title) }
    val lazyListState = rememberLazyListState()

    val itemsBeforeReorderable = buildList<LazyListScope.() -> Unit> {
        add {
            item(key = "title") {
                Title(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .sharedElementTransition(transitionKey = titleTransitionKey),
                    title = titleCache,
                    onTitleChanged = {
                        titleCache = it
                        onTitleChanged(it)
                    },
                    onNextClick = onTitleNextClick,
                )
            }
        }
        add {
            item(key = "spacer1") {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        if (reminderStateData != null) {
            add {
                item(key = "Reminder") {
                    ReminderButton(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        reminderData = reminderStateData,
                        onClick = onEditReminderClick,
                    )
                }
            }
        }
    }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = from.index - itemsBeforeReorderable.size
        val toIndex = to.index - itemsBeforeReorderable.size
        onMoveItems(fromIndex, toIndex)
    }

    val backgroundColorLocal = key(backgroundColor, LocalThemeMode.current) {
        when (LocalThemeMode.current) {
            ThemeMode.DARK -> backgroundColor?.night
            ThemeMode.LIGHT -> backgroundColor?.day
        }?.let(::Color) ?: MaterialTheme.colorScheme.surface
    }

    LaunchedEffect(focusRequests) {
        var indexOfItem = -1
        uncheckedItems.forEachIndexed { index, item ->
            if (item.focusRequest?.isHandled() == false) {
                indexOfItem = index
            }
        }
        if (indexOfItem == -1) return@LaunchedEffect
        val itemIndexInColumn = itemsBeforeReorderable.size + indexOfItem


        if (lazyListState.layoutInfo.visibleItemsInfo.any { it.index == itemIndexInColumn }) {
            return@LaunchedEffect
        }
        val lastVisible = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@LaunchedEffect
        if (lastVisible.index > itemIndexInColumn) {
            lazyListState.animateScrollToItem(itemIndexInColumn, (lazyListState.layoutInfo.viewportSize.height * -0.8f).toInt())
            return@LaunchedEffect
        }
        val itemsDifference = (itemIndexInColumn - lastVisible.index).coerceAtLeast(0)
        if (itemsDifference > 2) {
            lazyListState.animateScrollToItem(itemIndexInColumn)
        } else {
            lazyListState.scrollBy((lastVisible.size * itemsDifference.toFloat()) + 1f)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = PaddingValues(bottom = contentPaddingBottom, top = 16.dp),
            verticalArrangement = spacedBy(4.dp),
        ) {
            itemsBeforeReorderable.forEach { it.invoke(this) }

            if (uncheckedItems.isNotEmpty()) {
                items(uncheckedItems, key = { item -> item.id }) { item ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = item.id,
                    ) {
                        DraggableChecklistItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            title = item.text,
                            checked = false,
                            background = backgroundColorLocal,
                            focusRequest = item.focusRequest,
                            onCheckedChange = { onItemChecked(item) },
                            onTextChanged = { onItemTextChanged(it, item) },
                            onDoneClicked = { onDoneClicked(item) },
                            onDeleteClick = { onDeleteClick(item) },
                            onDragCompleted = { onMoveCompleted() },
                            onItemFocused = { onItemFocused(item) }
                        )
                    }
                }
            }

            item(key = "AddItemButton") {
                AddItemButton(
                    modifier = Modifier.animateItem(),
                    onAddClick = onAddChecklistItemClick,
                )
            }

            item(key = "CheckedItems") {
                if (checkedItems.isNotEmpty()) {
                    HideCheckedItemsButton(
                        modifier = Modifier.animateItem(),
                        checked = showCheckedItems.not(),
                        hiddenItemCount = checkedItems.size,
                        toggleCheckedItemsVisibility = toggleCheckedItemsVisibility,
                    )
                    if (showCheckedItems) {
                        CheckedItems(
                            modifier = Modifier.animateItem(),
                            checkedItems = checkedItems,
                            onItemUnchecked = onItemUnchecked,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckedItems(
    modifier: Modifier,
    checkedItems: List<CheckedListItemUi>,
    onItemUnchecked: (CheckedListItemUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 46.dp)
            .animateContentSize(),
        verticalArrangement = spacedBy(4.dp),
    ) {
        checkedItems.forEach { item ->
            key(item.id) {
                EditableChecklistCheckbox(
                    modifier = Modifier
                        .fillMaxWidth(),
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
    modifier: Modifier,
    checked: Boolean,
    hiddenItemCount: Int,
    toggleCheckedItemsVisibility: () -> Unit,
) {
    TextButton(
        modifier = modifier
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                fontWeight = FontWeight.Normal,
                fontSize = 15.5.sp,
                fontFamily = plusJakartaSans,
            )
        }
    }
}

@Composable
private fun AddItemButton(
    modifier: Modifier,
    onAddClick: () -> Unit,
) {
    TextButton(
        modifier = modifier.padding(horizontal = 16.dp),
        onClick = onAddClick,
        contentPadding = PaddingValues(start = 34.dp, end = 34.dp),
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
                fontWeight = FontWeight.Normal,
                fontFamily = plusJakartaSans,
                fontSize = 15.5.sp
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
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        onValueChange = onTitleChanged,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W600,
            fontSize = 22.sp,
            fontFamily = plusJakartaSans,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            onNextClick()
        }),
        decorationBox = { innerTextField ->
            Box(Modifier.fillMaxWidth()) {
                if (title.isEmpty()) {
                    Text(
                        text = stringResource(R.string.title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = 22.sp,
                        fontFamily = plusJakartaSans,
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
            checkedItems = EditChecklistDemoData.checkedChecklistItems,
            uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
            showCheckedItems = true,
        )
    }
}

@Preview(name = "PreviewListWithReminder", showBackground = true)
@Composable
private fun PreviewListWithReminder() {
    ApplicationTheme {
        ChecklistBody(
            modifier = Modifier.fillMaxWidth(),
            title = "This is a title",
            uncheckedItems = EditChecklistDemoData.uncheckedChecklistItems,
            reminderStateData = ReminderStateData(
                sourceDate = OffsetDateTime.now(),
                dateString = AnnotatedString("21 May, 10:12 AM"),
                outdated = false,
            ),
        )
    }
}