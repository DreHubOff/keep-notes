package com.jksol.keep.notes.ui.mapper

import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem

fun TextNote.toMainScreenItem(): MainScreenItem.TextNote {
    return MainScreenItem.TextNote(
        id = this.id,
        title = this.title,
        content = this.content,
        isPinned = this.isPinned,
        hasScheduledReminder = this.hasReminder,
        interactive = true
    )
}

fun Checklist.toMainScreenItem(checklistItemsMaxCount: Int = 10): MainScreenItem.Checklist {
    val tickedItems = items.filter { it.isChecked }
    val noTickedItems = items.filter { !it.isChecked }
    return MainScreenItem.Checklist(
        id = this.id,
        title = this.title,
        isPinned = this.isPinned,
        hasTickedItems = tickedItems.isNotEmpty(),
        items = noTickedItems.take(checklistItemsMaxCount).map {
            MainScreenItem.Checklist.Item(isChecked = false, text = it.title)
        },
        hasScheduledReminder = false,
        interactive = true,
        tickedItems = tickedItems.size,
    )
}