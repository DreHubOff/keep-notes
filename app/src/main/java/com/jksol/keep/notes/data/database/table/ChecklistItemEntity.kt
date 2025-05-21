package com.jksol.keep.notes.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jksol.keep.notes.data.database.table.ChecklistItemEntity.Companion.FOREIGN_KEY_COLUMN_NAME

const val CHECKLIST_ITEMS_TABLE_NAME = "checklist_items"

@Entity(
    tableName = CHECKLIST_ITEMS_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = ChecklistEntity::class,
            parentColumns = [ChecklistEntity.PRIMARY_KEY_COLUMN_NAME],
            childColumns = [FOREIGN_KEY_COLUMN_NAME],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(FOREIGN_KEY_COLUMN_NAME)]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = FOREIGN_KEY_COLUMN_NAME)
    val checklistId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean,
    @ColumnInfo(name = "list_position")
    val listPosition: Int,
) {

    companion object {
        const val FOREIGN_KEY_COLUMN_NAME = "checklist_id"
    }
}