package com.jksol.keep.notes.data.database.table

import androidx.room.Embedded
import androidx.room.Relation

data class ChecklistWithItems(
    @Embedded val checklist: ChecklistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = ChecklistItemEntity.FOREIGN_KEY_COLUMN_NAME,
    )
    val items: List<ChecklistItemEntity>,
)