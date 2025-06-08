package com.jksol.keep.notes.data.mapper

import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.database.table.TextNoteEntity

fun TextNoteEntity.toDomain(): TextNote {
    return TextNote(
        id = id,
        title = title,
        content = content,
        creationDate = creationDate,
        modificationDate = modificationDate,
        backgroundColor = displayColorResource,
        isPinned = isPinned,
        isTrashed = isTrashed,
        trashedDate = trashedDate,
        reminderDate = reminderDate,
        reminderHasBeenPosted = reminderHasBeenPosted,
    )
}

fun TextNote.toEntity(): TextNoteEntity {
    return TextNoteEntity(
        id = id,
        title = title,
        content = content,
        creationDate = creationDate,
        modificationDate = modificationDate,
        displayColorResource = backgroundColor,
        isPinned = isPinned,
        isTrashed = isTrashed,
        trashedDate = trashedDate,
        reminderDate = reminderDate,
        reminderHasBeenPosted = reminderHasBeenPosted,
    )
}