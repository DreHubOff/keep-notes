package com.jksol.keep.notes.core

import com.jksol.keep.notes.data.AlarmSchedulerRepository
import com.jksol.keep.notes.data.ChecklistRepository
import java.time.OffsetDateTime
import javax.inject.Inject

class ChecklistEditorFacade @Inject constructor(
    private val checklistRepository: ChecklistRepository,
    private val alarmSchedulerRepository: AlarmSchedulerRepository,
) : MainTypeEditorFacade {

    override suspend fun storePinnedSate(pinned: Boolean, itemId: Long) {
        checklistRepository.storePinnedSate(pinned, itemId)
    }

    override suspend fun storeNewTitle(title: String, itemId: Long) {
        checklistRepository.storeNewTitle(title, itemId)
    }

    override suspend fun moveToTrash(itemId: Long) {
        cancelAlarm(itemId)
        checklistRepository.moveToTrash(itemId)
    }

    override suspend fun permanentlyDelete(itemId: Long) {
        cancelAlarm(itemId)
        checklistRepository.permanentlyDelete(itemId)
    }

    override suspend fun restoreItemFromTrash(itemId: Long) {
        checklistRepository.restoreItemFromTrash(itemId)
    }

    override suspend fun deleteReminder(itemId: Long) {
        cancelAlarm(itemId)
        checklistRepository.deleteReminder(itemId)
    }

    override suspend fun setReminder(itemId: Long, date: OffsetDateTime) {
        checklistRepository.storeReminderDate(itemId, date)
        val item = checklistRepository.getChecklistById(itemId) ?: return
        alarmSchedulerRepository.scheduleAlarm(item)
    }

    private suspend fun cancelAlarm(itemId: Long) {
        val item = checklistRepository.getChecklistById(itemId) ?: return
        alarmSchedulerRepository.cancelAlarm(item)
    }
}