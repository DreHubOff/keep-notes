package com.jksol.keep.notes.core.model

import java.time.OffsetDateTime

interface SortableListItem {
    val isPinned: Boolean
    val creationDate: OffsetDateTime
}