package com.jksol.keep.notes.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jksol.keep.notes.data.database.table.TEXT_NOTE_TABLE_NAME
import com.jksol.keep.notes.data.database.table.TextNoteEntity

@Dao
interface TextNoteDao {

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME")
    suspend fun getAll(): List<TextNoteEntity>

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE is_trashed = 0")
    suspend fun getNotTrashed(): List<TextNoteEntity>

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TextNoteEntity?

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET title = :newTitle WHERE id = :id")
    suspend fun updateTitleById(id: Long, newTitle: String)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET content = :newContent WHERE id = :id")
    suspend fun updateContentById(id: Long, newContent: String)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET pinned = :pinned WHERE id = :id")
    suspend fun updatePennedStateById(id: Long, pinned: Int)

    @Insert
    suspend fun insertTextNote(textNote: TextNoteEntity): Long
}