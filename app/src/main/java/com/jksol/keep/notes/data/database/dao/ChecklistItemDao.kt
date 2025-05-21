package com.jksol.keep.notes.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jksol.keep.notes.data.database.table.CHECKLIST_ITEMS_TABLE_NAME
import com.jksol.keep.notes.data.database.table.ChecklistItemEntity

@Dao
interface ChecklistItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(items: ChecklistItemEntity): Long

    @Query("UPDATE $CHECKLIST_ITEMS_TABLE_NAME SET is_checked = :checked WHERE checklist_id = :checklistId AND id = :itemId")
    suspend fun updateCheckedStateByIds(checked: Boolean, checklistId: Long, itemId: Long)

    @Query("SELECT * FROM $CHECKLIST_ITEMS_TABLE_NAME WHERE checklist_id = :checklistId AND is_checked = 0")
    suspend fun getUncheckedItemsForChecklist(checklistId: Long): List<ChecklistItemEntity>
}