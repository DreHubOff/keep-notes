package com.jksol.keep.notes.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jksol.keep.notes.core.model.NoteColor
import java.time.OffsetDateTime

const val CHECKLIST_TABLE_NAME = "checklists"

@Entity(tableName = CHECKLIST_TABLE_NAME)
data class ChecklistEntity(

    @ColumnInfo(name = PRIMARY_KEY_COLUMN_NAME)
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo("title")
    val title: String,

    @ColumnInfo("creation_date")
    val creationDate: OffsetDateTime,

    @ColumnInfo("modification_date")
    val modificationDate: OffsetDateTime,

    @ColumnInfo("pinned")
    val isPinned: Boolean,

    @ColumnInfo("background_color")
    val backgroundColor: NoteColor?,

    @ColumnInfo("is_trashed")
    val isTrashed: Boolean,

    @ColumnInfo("trashed_date")
    val trashedDate: OffsetDateTime?,
) {

    companion object {
        const val PRIMARY_KEY_COLUMN_NAME = "id"
    }
}