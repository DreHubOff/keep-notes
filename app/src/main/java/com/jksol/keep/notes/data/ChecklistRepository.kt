package com.jksol.keep.notes.data

import android.util.Log
import androidx.room.withTransaction
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.database.AppDatabase
import com.jksol.keep.notes.data.database.dao.ChecklistDao
import com.jksol.keep.notes.data.database.dao.ChecklistItemDao
import com.jksol.keep.notes.data.database.table.ChecklistWithItems
import com.jksol.keep.notes.data.mapper.toDomain
import com.jksol.keep.notes.data.mapper.toEntity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import javax.inject.Inject

class ChecklistRepository @Inject constructor(
    private val database: AppDatabase,
    private val checklistDao: ChecklistDao,
    private val checklistItemDao: ChecklistItemDao,
) {

    suspend fun getChecklistById(id: Long): Checklist? =
        checklistDao.getChecklistWithItemsById(id)?.toDomain()

    fun observeChecklistById(id: Long): Flow<Checklist> {
        return checklistDao
            .observeChecklistWithItemsById(id)
            .filter { it.isNotEmpty() }
            .map { it.first().toDomain() }
    }

    suspend fun insertChecklist(checklist: Checklist): Checklist {
        val result = withContext(NonCancellable) {
            val checklistEntity = checklist.toEntity()
            val items = checklist.items.map { it.toEntity(parentChecklistId = checklistEntity.id) }
            val newChecklistId = checklistDao.insertChecklistWithItems(
                checklistWithItems = ChecklistWithItems(checklist = checklistEntity, items = items),
                checklistItemDao = checklistItemDao,
            )
            getChecklistById(newChecklistId)
        }
        return checkNotNull(result) { "New checklist was not inserted" }
    }

    fun observeNotTrashedChecklists(): Flow<List<Checklist>> =
        checklistDao.observeNotTrashed().map { list -> list.map { checklist -> checklist.toDomain() } }

    suspend fun updateChecklistTitle(checklistId: Long, title: String) {
        withContext(NonCancellable) {
            checklistDao.updateChecklistTitleById(
                id = checklistId,
                title = title,
                modificationDate = OffsetDateTime.now()
            )
        }
    }

    suspend fun updatePinnedState(checklistId: Long, isPinned: Boolean) {
        withContext(NonCancellable) {
            // Does not effect modification date
            checklistDao.updatePinnedStateById(checklistId, isPinned)
        }
    }

    suspend fun saveChecklistItemAsLast(checklistId: Long, item: ChecklistItem): ChecklistItem {
        val (insertedItemId, newPosition) = withContext(NonCancellable) {
            database.withTransaction {
                val newPosition = (checklistItemDao.getLastListPosition(checklistId) ?: -1) + 1
                val itemToSave = item.copy(listPosition = newPosition).toEntity(parentChecklistId = checklistId)
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = OffsetDateTime.now())
                checklistItemDao.insertChecklistItem(itemToSave) to newPosition
            }
        }
        return item.copy(id = insertedItemId, listPosition = newPosition)
    }

    suspend fun updateChecklistItemCheckedState(isChecked: Boolean, itemId: Long, checklistId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistItemDao.updateCheckedStateByIds(checked = isChecked, checklistId = checklistId, itemId = itemId)
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = OffsetDateTime.now())
            }
        }
    }

    suspend fun updateChecklistItemTitle(itemId: Long, checklistId: Long, title: String) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = OffsetDateTime.now())
                checklistItemDao.updateTitleByIds(itemId = itemId, checklistId = checklistId, title = title)
            }
        }
    }

    suspend fun deleteChecklistItem(itemId: Long, checklistId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = OffsetDateTime.now())
                checklistItemDao.deleteItem(itemId = itemId, checklistId = checklistId)
                reorderItemPositions(checklistId = checklistId)
            }
        }
    }

    suspend fun delete(checklist: Checklist) {
        withContext(NonCancellable) {
            checklistDao.deleteChecklist(checklist.toEntity())
            checklistItemDao.deleteItems(checklist.items.map { it.toEntity(checklist.id) })
        }
    }

    suspend fun insertChecklistItemAfterFollowing(
        checklistId: Long,
        itemBefore: Long,
        itemToInsert: ChecklistItem,
    ) {
        withContext(NonCancellable) {
            database.withTransaction {
                val oldItems = checklistItemDao.getItemsForChecklist(checklistId = checklistId)
                val itemBeforeIndex = oldItems.indexOfFirst { it.id == itemBefore }
                val itemToSave = itemToInsert.copy(listPosition = itemBeforeIndex + 1)

                if (itemBeforeIndex == oldItems.lastIndex) {
                    saveChecklistItemAsLast(checklistId = checklistId, item = itemToSave)
                    return@withTransaction
                }

                val itemsAfterInsertion = oldItems
                    .subList(itemToSave.listPosition, oldItems.size)
                    .map { checklistItem ->
                        checklistItem.copy(listPosition = checklistItem.listPosition + 1)
                    }

                val allToInsert = itemsAfterInsertion + itemToSave.toEntity(checklistId)
                checklistItemDao.insertChecklistItems(allToInsert)
            }
        }
    }

    suspend fun saveItemsNewOrder(checklistId: Long, orderedItemIds: List<Long>) {
        withContext(NonCancellable) {
            database.withTransaction {
                val dbUncheckedItems = checklistItemDao.getUncheckedItemsForChecklist(checklistId = checklistId)
                if (orderedItemIds.size != dbUncheckedItems.size) {
                    return@withTransaction
                }
                val orderedPositions = dbUncheckedItems.map { it.listPosition }.sorted()
                val updatedCollection = List(orderedItemIds.size) { index ->
                    val itemId = orderedItemIds[index]
                    val dbItem = dbUncheckedItems.find { it.id == itemId }
                    dbItem?.copy(listPosition = orderedPositions[index]) ?: error("Item with id=$itemId not found")
                }
                checklistItemDao.insertChecklistItems(updatedCollection)
            }
        }
    }


    private suspend fun reorderItemPositions(checklistId: Long) {
        val reordered = checklistItemDao.getItemsForChecklist(checklistId = checklistId).mapIndexed { index, item ->
            item.copy(listPosition = index)
        }
        checklistItemDao.insertChecklistItems(reordered)
    }
}