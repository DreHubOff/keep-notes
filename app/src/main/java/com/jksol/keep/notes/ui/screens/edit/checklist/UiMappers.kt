package com.jksol.keep.notes.ui.screens.edit.checklist

import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi

fun Checklist.toEditChecklistScreenState(
    focusedItemIndex: Int?,
    showCheckedItems: Boolean,
    modificationStatusMessage: String,
): EditChecklistScreenState {
    return EditChecklistScreenState(
        checklistId = id,
        title = title,
        isPinned = isPinned,
        modificationStatusMessage = modificationStatusMessage,
        uncheckedItems = items.toUncheckedListItemsUi(focusedItemIndex = focusedItemIndex),
        checkedItems = items.toCheckedListItemsUi(),
        showCheckedItems = showCheckedItems
    )
}

fun ChecklistItem.toUncheckedListItemUi(isFocused: Boolean = false): UncheckedListItemUi {
    return UncheckedListItemUi(
        id = id,
        text = title,
        focusRequest = if (isFocused) ElementFocusRequest() else null,
    )
}

fun List<ChecklistItem>.toCheckedListItemsUi(): List<CheckedListItemUi> {
    return filter { it.isChecked }
        .sortedBy { it.listPosition }
        .map {
            CheckedListItemUi(
                id = it.id,
                text = it.title
            )
        }
}

fun List<ChecklistItem>.toUncheckedListItemsUi(focusedItemIndex: Int?): List<UncheckedListItemUi> {
    return asSequence()
        .filter { !it.isChecked }
        .sortedBy { it.listPosition }
        .mapIndexed { index, item -> item.toUncheckedListItemUi(isFocused = index == focusedItemIndex) }
        .toList()
}