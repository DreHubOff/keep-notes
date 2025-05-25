package com.jksol.keep.notes.core.model

import java.time.OffsetDateTime

sealed interface ApplicationMainDataType : SortableListItem {
    val id: Long
    val title: String
    override val isPinned: Boolean
    override val creationDate: OffsetDateTime
    val modificationDate: OffsetDateTime
    val hasReminder: Boolean
    val backgroundColor: NoteColor?
    val isTrashed: Boolean
    val trashedDate: OffsetDateTime?
}