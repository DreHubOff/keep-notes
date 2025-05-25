package com.jksol.keep.notes.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
data class Checklist(
    override val id: Long,
    override val title: String,
    val items: List<ChecklistItem>,
    override val creationDate: OffsetDateTime,
    override val modificationDate: OffsetDateTime,
    override val isPinned: Boolean,
    override val backgroundColor: NoteColor?,
    override val isTrashed: Boolean,
    override val trashedDate: OffsetDateTime?,

    // TODO: Not yet implemented
    override val hasReminder: Boolean = false,
) : Parcelable, ApplicationMainDataType {

    companion object {
        fun generateEmpty() = Checklist(
            id = 0L,
            title = "",
            items = emptyList(),
            creationDate = OffsetDateTime.now(),
            modificationDate = OffsetDateTime.now(),
            isPinned = false,
            backgroundColor = null,
            isTrashed = false,
            trashedDate = null,
        )
    }
}