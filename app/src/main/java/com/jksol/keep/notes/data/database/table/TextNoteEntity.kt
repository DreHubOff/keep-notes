package com.jksol.keep.notes.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jksol.keep.notes.core.model.NoteColor
import java.time.OffsetDateTime

const val TEXT_NOTE_TABLE_NAME = "text_note"

@Entity(tableName = TEXT_NOTE_TABLE_NAME)
data class TextNoteEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "creation_date", typeAffinity = ColumnInfo.INTEGER)
    val creationDate: OffsetDateTime,

    @ColumnInfo(name = "modification_date", typeAffinity = ColumnInfo.INTEGER)
    val modificationDate: OffsetDateTime,

    @ColumnInfo(name = "display_color_resource")
    val displayColorResource: NoteColor?,

    @ColumnInfo(name = "pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_trashed")
    val isTrashed: Boolean = false,

    @ColumnInfo(name = "trashed_date")
    val trashedDate: OffsetDateTime?,

    @ColumnInfo(name = "has_reminder")
    val hasReminder: Boolean = false,
)