package com.jksol.keep.notes.core.model

import java.time.OffsetDateTime

data class Checklist(
    val id: Long,
    val title: String,
    val items: List<ChecklistItem>,
    val creationDate: OffsetDateTime,
    val modificationDate: OffsetDateTime,
    val isPinned: Boolean,
    val backgroundColor: NoteColor,
)