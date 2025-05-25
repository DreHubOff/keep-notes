package com.jksol.keep.notes.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jksol.keep.notes.data.database.table.TEXT_NOTE_TABLE_NAME
import com.jksol.keep.notes.data.database.table.TextNoteEntity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface TextNoteDao {

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE is_trashed = 0")
    fun observeNotTrashed(): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TextNoteEntity?

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET title = :newTitle WHERE id = :id")
    suspend fun updateTitleById(id: Long, newTitle: String)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET content = :newContent WHERE id = :id")
    suspend fun updateContentById(id: Long, newContent: String)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET pinned = :pinned WHERE id = :id")
    suspend fun updatePennedStateById(id: Long, pinned: Int)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET modification_date = :date WHERE id = :id")
    suspend fun updateModificationDateById(id: Long, date: OffsetDateTime)

    @Query(
        """
        UPDATE $TEXT_NOTE_TABLE_NAME 
        SET modification_date = :updateTime,
            title = :title,
            content = :content,
            pinned = :isPinned 
        WHERE id = :id
        """
    )
    suspend fun updateNoteContent(
        id: Long,
        updateTime: OffsetDateTime,
        title: String,
        content: String,
        isPinned: Boolean,
    )

    @Insert
    suspend fun insertTextNote(textNote: TextNoteEntity): Long

    @Delete
    suspend fun delete(textNote: TextNoteEntity)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET is_trashed = :isTrashed WHERE id = :id")
    suspend fun updateIsTrashedById(id: Long, isTrashed: Boolean)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET trashed_date = :date WHERE id = :id")
    suspend fun updateTrashedDateById(id: Long, date: OffsetDateTime?)
}