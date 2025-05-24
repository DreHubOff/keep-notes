package com.jksol.keep.notes.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
data class TextNote(
    override val id: Long,
    override val title: String,
    val content: String,
    override val creationDate: OffsetDateTime,
    override val modificationDate: OffsetDateTime,
    override val backgroundColor: NoteColor?,
    override val isPinned: Boolean,
    override val isTrashed: Boolean,
    override val hasReminder: Boolean,
) : Parcelable, ApplicationMainDataType {
    companion object {
        fun generateEmpty() = TextNote(
            id = 0,
            title = "",
            content = "",
            creationDate = OffsetDateTime.now(),
            modificationDate = OffsetDateTime.now(),
            backgroundColor = null,
            isPinned = false,
            isTrashed = false,
            hasReminder = false,
        )
    }
}