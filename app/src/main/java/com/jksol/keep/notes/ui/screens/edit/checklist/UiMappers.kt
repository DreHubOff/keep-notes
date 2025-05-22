package com.jksol.keep.notes.ui.screens.edit.checklist

import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi

fun Checklist.toEditChecklistScreenState(
    focusedItemId: Long? = null,
    showCheckedItems: Boolean = false,
    modificationStatusMessage: String = "",
): EditChecklistScreenState {
    return EditChecklistScreenState(
        checklistId = id,
        title = title,
        isPinned = isPinned,
        modificationStatusMessage = modificationStatusMessage,
        uncheckedItems = items.toUncheckedListItemsUi(focusedItemId = focusedItemId),
        checkedItems = items.toCheckedListItemsUi(),
        showCheckedItems = showCheckedItems
    )
}

fun ChecklistItem.toUncheckedListItemUi(isFocused: Boolean = false): UncheckedListItemUi {
    return UncheckedListItemUi(
        id = id,
        text = title,
        isFocused = isFocused,
    )
}

fun ChecklistItem.toCheckedListItemUi(): CheckedListItemUi = CheckedListItemUi(id = id, text = title)

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

fun List<ChecklistItem>.toUncheckedListItemsUi(focusedItemId: Long?): List<UncheckedListItemUi> {
    return filter { !it.isChecked }
        .sortedBy { it.listPosition }
        .map { it.toUncheckedListItemUi(isFocused = it.id == focusedItemId) }
}