package com.jksol.keep.notes.ui.screens.main.mapper

import androidx.compose.ui.graphics.Color
import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import java.time.OffsetDateTime

fun TextNote.toMainScreenItem(
    isSelected: Boolean,
    customBackground: NoteColor?,
): MainScreenItem.TextNote {
    return MainScreenItem.TextNote(
        id = this.id,
        title = this.title,
        content = this.content,
        isPinned = this.isPinned,
        hasScheduledReminder = this.reminderDate != null,
        interactive = true,
        reminderCompleted = this.reminderDate?.isBefore(OffsetDateTime.now()) == true,
        isSelected = isSelected,
        customBackground = customBackground,
    )
}

fun Checklist.toMainScreenItem(
    isSelected: Boolean,
    customBackground: NoteColor?,
    checklistItemsMaxCount: Int = 10,
): MainScreenItem.Checklist {
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
        hasScheduledReminder = this.reminderDate != null,
        interactive = true,
        tickedItems = tickedItems.size,
        reminderCompleted = this.reminderDate?.isBefore(OffsetDateTime.now()) == true,
        isSelected = isSelected,
        customBackground = customBackground,
    )
}

fun ApplicationMainDataType.toMainScreenItem(
    isSelected: Boolean,
    customBackground: NoteColor?,
    checklistItemsMaxCount: Int = 10,
): MainScreenItem {
    return when (this) {
        is TextNote -> this.toMainScreenItem(isSelected = isSelected, customBackground = customBackground)
        is Checklist -> this.toMainScreenItem(
            isSelected = isSelected,
            customBackground = customBackground,
            checklistItemsMaxCount = checklistItemsMaxCount,
        )
    }
}