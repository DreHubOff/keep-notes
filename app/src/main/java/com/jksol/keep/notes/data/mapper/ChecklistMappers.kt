package com.jksol.keep.notes.data.mapper

import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.database.table.ChecklistEntity
import com.jksol.keep.notes.data.database.table.ChecklistItemEntity
import com.jksol.keep.notes.data.database.table.ChecklistWithItems

fun ChecklistWithItems.toDomain(): Checklist {
    return Checklist(
        id = checklist.id,
        title = checklist.title,
        items = items.sortedBy { it.listPosition }.map { it.toDomain() },
        creationDate = checklist.creationDate,
        modificationDate = checklist.modificationDate,
        isPinned = checklist.isPinned,
        backgroundColor = checklist.backgroundColor,
        isTrashed = checklist.isTrashed,
        trashedDate = checklist.trashedDate,
        reminderDate = checklist.reminderDate,
        reminderHasBeenPosted = checklist.reminderHasBeenPosted,
    )
}

fun ChecklistItemEntity.toDomain(): ChecklistItem {
    return ChecklistItem(
        id = id,
        title = title,
        isChecked = isChecked,
        listPosition = listPosition,
    )
}

fun Checklist.toEntity(): ChecklistEntity {
    return ChecklistEntity(
        id = id,
        title = title,
        creationDate = creationDate,
        modificationDate = modificationDate,
        isPinned = isPinned,
        backgroundColor = backgroundColor,
        isTrashed = isTrashed,
        trashedDate = trashedDate,
        reminderDate = reminderDate,
        reminderHasBeenPosted = reminderHasBeenPosted,
    )
}

fun ChecklistItem.toEntity(parentChecklistId: Long): ChecklistItemEntity {
    return ChecklistItemEntity(
        id = id,
        title = title,
        isChecked = isChecked,
        listPosition = listPosition,
        checklistId = parentChecklistId
    )
}
