package com.jksol.keep.notes.core

import com.jksol.keep.notes.core.model.NoteColor
import java.time.OffsetDateTime

interface MainTypeEditorFacade {

    suspend fun storePinnedSate(pinned: Boolean, itemId: Long)

    suspend fun storeNewTitle(title: String, itemId: Long)

    suspend fun moveToTrash(itemId: Long)

    suspend fun permanentlyDelete(itemId: Long)

    suspend fun restoreItemFromTrash(itemId: Long)

    suspend fun deleteReminder(itemId: Long)

    suspend fun setReminder(itemId: Long, date: OffsetDateTime)

    suspend fun saveBackgroundColor(itemId: Long, color: NoteColor?)
}