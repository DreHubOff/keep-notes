package com.jksol.keep.notes.core.model

import java.time.LocalDateTime

data class TextNote(
    val id: Long,
    val title: String,
    val content: String,
    val creationDate: LocalDateTime,
    val modificationDate: LocalDateTime,
    val displayColorResource: NoteColor,
    val isPinned: Boolean = false,
    val isTrashed: Boolean = false,
    val hasReminder: Boolean = false,
)