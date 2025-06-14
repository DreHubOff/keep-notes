package com.jksol.keep.notes.core

import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.data.ReminderSchedulerRepository
import com.jksol.keep.notes.data.TextNotesRepository
import java.time.OffsetDateTime
import javax.inject.Inject

class TextNoteEditorFacade @Inject constructor(
    private val textNotesRepository: TextNotesRepository,
    private val reminderSchedulerRepository: ReminderSchedulerRepository,
) : MainTypeEditorFacade {

    override suspend fun storePinnedSate(pinned: Boolean, itemId: Long) {
        textNotesRepository.storePinnedSate(pinned, itemId)
    }

    override suspend fun storeNewTitle(title: String, itemId: Long) {
        textNotesRepository.storeNewTitle(title, itemId)
    }

    override suspend fun moveToTrash(itemId: Long) {
        cancelAlarm(itemId)
        textNotesRepository.moveToTrash(itemId)
    }

    override suspend fun permanentlyDelete(itemId: Long) {
        cancelAlarm(itemId)
        textNotesRepository.permanentlyDelete(itemId)
    }

    override suspend fun restoreItemFromTrash(itemId: Long) {
        textNotesRepository.restoreItemFromTrash(itemId)
    }

    override suspend fun deleteReminder(itemId: Long) {
        cancelAlarm(itemId)
        textNotesRepository.deleteReminder(itemId)
    }

    override suspend fun setReminder(itemId: Long, date: OffsetDateTime) {
        textNotesRepository.storeReminderDate(itemId, date)
        val note = textNotesRepository.getNoteById(itemId) ?: return
        textNotesRepository.updateChecklistReminderShownState(note.id, isShown = false)
        reminderSchedulerRepository.cancelReminder(note)
        reminderSchedulerRepository.scheduleReminder(note)
    }

    override suspend fun saveBackgroundColor(itemId: Long, color: NoteColor?) {
        textNotesRepository.saveBackgroundColor(itemId, color)
    }

    private suspend fun cancelAlarm(itemId: Long) {
        val item = textNotesRepository.getNoteById(itemId) ?: return
        textNotesRepository.updateChecklistReminderShownState(itemId, isShown = false)
        textNotesRepository.deleteReminder(item.id)
        reminderSchedulerRepository.cancelReminder(item, removeNotification = true)
    }
}