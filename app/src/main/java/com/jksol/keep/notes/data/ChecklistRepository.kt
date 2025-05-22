package com.jksol.keep.notes.data

import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.database.dao.ChecklistDao
import com.jksol.keep.notes.data.database.dao.ChecklistItemDao
import com.jksol.keep.notes.data.database.table.ChecklistWithItems
import com.jksol.keep.notes.data.mapper.toDomain
import com.jksol.keep.notes.data.mapper.toEntity
import java.time.OffsetDateTime
import javax.inject.Inject

class ChecklistRepository @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val checklistItemDao: ChecklistItemDao,
) {

    suspend fun getChecklistById(id: Long): Checklist? =
        checklistDao.getChecklistWithItemsById(id)?.toDomain()

    suspend fun insertChecklist(checklist: Checklist): Checklist {
        val checklistEntity = checklist.toEntity()
        val items = checklist.items.map { it.toEntity(parentChecklistId = checklistEntity.id) }
        val newChecklistId = checklistDao
            .insertChecklistWithItems(
                checklistWithItems = ChecklistWithItems(checklist = checklistEntity, items = items),
                checklistItemDao = checklistItemDao,
            )
        return checkNotNull(getChecklistById(newChecklistId)) {
            "New checklist was not inserted"
        }
    }

    suspend fun getChecklists(): List<Checklist> =
        checklistDao.getAllChecklistsWithItems().map { it.toDomain() }

    suspend fun getItemsForChecklist(checklistId: Long): List<ChecklistItem> =
        checklistItemDao.getItemsForChecklist(checklistId = checklistId).map { it.toDomain() }

    suspend fun updateChecklistTitle(checklistId: Long, title: String) {
        checklistDao.updateChecklistTitleById(checklistId, title)
    }

    suspend fun updateChecklistModifiedDate(checklistId: Long, date: OffsetDateTime) {
        checklistDao.updateChecklistModifiedDateById(checklistId, date)
    }

    suspend fun updatePinnedState(checklistId: Long, isPinned: Boolean) {
        checklistDao.updatePinnedStateById(checklistId, isPinned)
    }

    suspend fun saveChecklistItem(checklistId: Long, item: ChecklistItem): ChecklistItem {
        val itemToSave = item.toEntity(parentChecklistId = checklistId)
        val insertedItemId = checklistItemDao.insertChecklistItem(itemToSave)
        return item.copy(id = insertedItemId)
    }

    suspend fun saveChecklistItemAsLast(checklistId: Long, item: ChecklistItem): ChecklistItem {
        val newPosition = checklistItemDao.getLastListPosition(checklistId) + 1
        val itemToSave = item.copy(listPosition = newPosition).toEntity(parentChecklistId = checklistId)
        val insertedItemId = checklistItemDao.insertChecklistItem(itemToSave)
        return item.copy(id = insertedItemId, listPosition = newPosition)
    }

    suspend fun updateChecklistItemCheckedState(isChecked: Boolean, itemId: Long, checklistId: Long) {
        checklistItemDao.updateCheckedStateByIds(checked = isChecked, checklistId = checklistId, itemId = itemId)
    }

    suspend fun getUncheckedItemsForChecklist(checklistId: Long): List<ChecklistItem> =
        checklistItemDao
            .getUncheckedItemsForChecklist(checklistId)
            .map { it.toDomain() }

    suspend fun getCheckedItemsForChecklist(checklistId: Long): List<ChecklistItem> =
        checklistItemDao
            .getCheckedItemsForChecklist(checklistId)
            .map { it.toDomain() }

    suspend fun updateChecklistItemTitle(itemId: Long, checklistId: Long, title: String) {
        checklistItemDao.updateTitleByIds(itemId = itemId, checklistId = checklistId, title = title)
    }

    suspend fun deleteChecklistItem(itemId: Long, checklistId: Long) {
        checklistItemDao.deleteItem(itemId = itemId, checklistId = checklistId)
    }

    suspend fun delete(checklist: Checklist) {
        checklistDao.deleteChecklist(checklist.toEntity())
        checklistItemDao.deleteItems(checklist.items.map { it.toEntity(checklist.id) })
    }
}