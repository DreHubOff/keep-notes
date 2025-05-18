package com.jksol.keep.notes.data

import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.database.dao.TextNoteDao
import com.jksol.keep.notes.data.database.table.TextNoteEntity
import com.jksol.keep.notes.data.mapper.toDomain
import com.jksol.keep.notes.data.mapper.toEntity
import javax.inject.Inject

class TextNotesRepository @Inject constructor(
    private val textNoteDao: TextNoteDao,
) {

    suspend fun getNotTrashedNotes(): List<TextNote> =
        textNoteDao.getNotTrashed().map(TextNoteEntity::toDomain)

    suspend fun getNoteById(id: Long): TextNote? =
        textNoteDao.getById(id.toString())?.toDomain()

    suspend fun saveTextNote(textNote: TextNote): TextNote {
        val id = textNoteDao.insertTextNote(textNote.toEntity())
        return textNote.copy(id = id)
    }

    suspend fun updateTitle(noteId: Long, title: String) {
        textNoteDao.updateTitleById(id = noteId, newTitle = title)
    }

    suspend fun updateContent(noteId: Long, content: String) {
        textNoteDao.updateContentById(id = noteId, newContent = content)
    }

    suspend fun updatePinnedState(noteId: Long, isPinned: Boolean) {
        textNoteDao.updatePennedStateById(id = noteId, pinned = if (isPinned) 1 else 0)
    }
}