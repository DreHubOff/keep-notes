package com.jksol.keep.notes.data

import androidx.room.withTransaction
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.database.AppDatabase
import com.jksol.keep.notes.data.database.dao.TextNoteDao
import com.jksol.keep.notes.data.mapper.toDomain
import com.jksol.keep.notes.data.mapper.toEntity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import javax.inject.Inject

class TextNotesRepository @Inject constructor(
    private val dataSource: AppDatabase,
    private val textNoteDao: TextNoteDao,
) {

    fun observeNotTrashedNotes(): Flow<List<TextNote>> =
        textNoteDao.observeNotTrashed().map { list -> list.map { textNote -> textNote.toDomain() } }

    fun observeTrashedNotes(): Flow<List<TextNote>> =
        textNoteDao.observeTrashed().map { list -> list.map { textNote -> textNote.toDomain() } }

    suspend fun getNoteById(id: Long): TextNote? =
        textNoteDao.getById(id.toString())?.toDomain()

    suspend fun saveTextNote(textNote: TextNote): TextNote {
        val id = withContext(NonCancellable) {
            textNoteDao.insertTextNote(textNote.toEntity())
        }
        return textNote.copy(id = id)
    }

    suspend fun delete(textNote: TextNote) = delete(listOf(textNote))

    suspend fun delete(textNotes: List<TextNote>) {
        withContext(NonCancellable) {
            textNoteDao.delete(textNotes.map(TextNote::toEntity))
        }
    }

    suspend fun updateNoteContent(
        noteId: Long,
        updateTime: OffsetDateTime,
        title: String,
        content: String,
        isPinned: Boolean,
    ) {
        withContext(NonCancellable) {
            textNoteDao.updateNoteContent(
                id = noteId,
                updateTime = updateTime,
                title = title,
                content = content,
                isPinned = isPinned,
            )
        }
    }

    suspend fun moveToTrash(noteId: Long) {
        withContext(NonCancellable) {
            dataSource.withTransaction {
                textNoteDao.updateIsTrashedById(id = noteId, isTrashed = true)
                textNoteDao.updateTrashedDateById(id = noteId, date = OffsetDateTime.now())
                textNoteDao.updatePinnedStateById(id = noteId, pinned = 0)
            }
        }
    }

    suspend fun restoreNote(noteId: Long) {
        withContext(NonCancellable) {
            dataSource.withTransaction {
                textNoteDao.updateIsTrashedById(id = noteId, isTrashed = false)
                textNoteDao.updateTrashedDateById(id = noteId, date = null)
            }
        }
    }
}