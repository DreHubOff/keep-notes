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
    override val reminderDate: OffsetDateTime?,
    override val reminderHasBeenPosted: Boolean,
) : Parcelable, ApplicationMainDataType {

    override fun isEmpty(): Boolean = title.trim().isEmpty() && (items.isEmpty() || items.all { it.title.trim().isEmpty() })

    companion object {
        fun generateEmpty() = Checklist(
            id = 0L,
            title = "",
            items = listOf(ChecklistItem.generateEmpty()),
            creationDate = OffsetDateTime.now(),
            modificationDate = OffsetDateTime.now(),
            isPinned = false,
            backgroundColor = null,
            isTrashed = false,
            trashedDate = null,
            reminderDate = null,
            reminderHasBeenPosted = false,
        )
    }
}