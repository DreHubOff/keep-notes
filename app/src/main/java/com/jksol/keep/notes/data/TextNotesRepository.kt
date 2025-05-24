package com.jksol.keep.notes.data

import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.database.dao.TextNoteDao
import com.jksol.keep.notes.data.mapper.toDomain
import com.jksol.keep.notes.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class TextNotesRepository @Inject constructor(
    private val textNoteDao: TextNoteDao,
) {

    fun observeNotTrashedNotes(): Flow<List<TextNote>> =
        textNoteDao.observeNotTrashed().map { list -> list.map { textNote -> textNote.toDomain() } }

    suspend fun getNoteById(id: Long): TextNote? =
        textNoteDao.getById(id.toString())?.toDomain()

    suspend fun saveTextNote(textNote: TextNote): TextNote {
        val id = textNoteDao.insertTextNote(textNote.toEntity())
        return textNote.copy(id = id)
    }

    suspend fun delete(textNote: TextNote) {
        textNoteDao.delete(textNote.toEntity())
    }

    suspend fun updateNoteContent(
        noteId: Long,
        updateTime: OffsetDateTime,
        title: String,
        content: String,
        isPinned: Boolean,
    ) {
        textNoteDao.updateNoteContent(id = noteId, updateTime = updateTime, title = title, content = content, isPinned = isPinned)
    }
}