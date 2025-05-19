package com.jksol.keep.notes.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
data class TextNote(
    val id: Long,
    val title: String,
    val content: String,
    val creationDate: OffsetDateTime,
    val modificationDate: OffsetDateTime,
    val displayColorResource: NoteColor?,
    val isPinned: Boolean,
    val isTrashed: Boolean,
    val hasReminder: Boolean,
) : Parcelable {
    companion object {
        fun generateEmpty() = TextNote(
            id = 0,
            title = "",
            content = "",
            creationDate = OffsetDateTime.now(),
            modificationDate = OffsetDateTime.now(),
            displayColorResource = null,
            isPinned = false,
            isTrashed = false,
            hasReminder = false
        )
    }
}