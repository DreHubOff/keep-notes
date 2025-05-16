package com.jksol.keep.notes.core.model

import java.time.LocalDateTime

data class TextNote(
    val id: Long,
    val title: String,
    val content: String,
    val creationDate: LocalDateTime,
    val modificationDate: LocalDateTime,

    // TODO: Not yet color resource
    val displayColorResource: Int,
) {
}