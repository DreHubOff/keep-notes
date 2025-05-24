package com.jksol.keep.notes.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jksol.keep.notes.data.database.table.CHECKLIST_TABLE_NAME
import com.jksol.keep.notes.data.database.table.ChecklistEntity
import com.jksol.keep.notes.data.database.table.ChecklistWithItems
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface ChecklistDao {

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME")
    suspend fun getAllChecklistsWithItems(): List<ChecklistWithItems>

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME WHERE id = :id")
    fun observeChecklistWithItemsById(id: Long): Flow<List<ChecklistWithItems>>

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME WHERE id = :id LIMIT 1")
    suspend fun getChecklistWithItemsById(id: Long): ChecklistWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: ChecklistEntity): Long

    @Transaction
    suspend fun insertChecklistWithItems(
        checklistWithItems: ChecklistWithItems,
        checklistItemDao: ChecklistItemDao,
    ): Long {
        val checklistId = insertChecklist(checklistWithItems.checklist)
        val items = checklistWithItems.items.map {
            it.copy(checklistId = checklistId)
        }
        checklistItemDao.insertChecklistItems(items)
        return checklistId
    }

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET title = :title WHERE id = :id")
    suspend fun updateChecklistTitleById(id: Long, title: String)

    @Transaction
    suspend fun updateChecklistTitleById(id: Long, title: String, modificationDate: OffsetDateTime) {
        updateChecklistTitleById(id, title)
        updateChecklistModifiedDateById(id, modificationDate)
    }

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET modification_date = :date WHERE id = :id")
    suspend fun updateChecklistModifiedDateById(id: Long, date: OffsetDateTime)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET pinned = :isPinned WHERE id = :id")
    suspend fun updatePinnedStateById(id: Long, isPinned: Boolean)

    @Delete
    suspend fun deleteChecklist(entity: ChecklistEntity)
}