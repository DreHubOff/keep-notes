package com.jksol.keep.notes.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TextNote(
    val id: Long,
    val title: String,
    val content: String,
    val creationDate: LocalDateTime,
    val modificationDate: LocalDateTime,
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
            creationDate = LocalDateTime.now(),
            modificationDate = LocalDateTime.now(),
            displayColorResource = null,
            isPinned = false,
            isTrashed = false,
            hasReminder = false
        )
    }
}