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
        uncheckedItems = items
            .filter { !it.isChecked }
            .sortedBy { it.listPosition }
            .map { it.toUncheckedListItemUi(isFocused = it.id == focusedItemId) },
        checkedItems = items
            .filter { it.isChecked }
            .sortedBy { it.listPosition }
            .map {
                CheckedListItemUi(
                    id = it.id,
                    text = it.title
                )
            },
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

fun CheckedListItemUi.toUncheckedListItemUi(): UncheckedListItemUi {
    return UncheckedListItemUi(
        id = id,
        text = text,
        isFocused = false,
    )
}

fun UncheckedListItemUi.toCheckedListItemUi(): CheckedListItemUi {
    return CheckedListItemUi(
        id = id,
        text = text,
    )
}
