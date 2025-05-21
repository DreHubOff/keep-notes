package com.jksol.keep.notes.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
data class Checklist(
    val id: Long,
    val title: String,
    val items: List<ChecklistItem>,
    override val creationDate: OffsetDateTime,
    val modificationDate: OffsetDateTime,
    override val isPinned: Boolean,
    val backgroundColor: NoteColor?,
) : Parcelable, SortableListItem {

    companion object {
        val EMPTY = Checklist(
            id = 0L,
            title = "",
            items = emptyList(),
            creationDate = OffsetDateTime.now(),
            modificationDate = OffsetDateTime.now(),
            isPinned = false,
            backgroundColor = null,
        )
    }
}