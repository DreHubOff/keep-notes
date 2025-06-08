package com.jksol.keep.notes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jksol.keep.notes.data.database.converter.Converters
import com.jksol.keep.notes.data.database.dao.ChecklistDao
import com.jksol.keep.notes.data.database.dao.ChecklistItemDao
import com.jksol.keep.notes.data.database.dao.TextNoteDao
import com.jksol.keep.notes.data.database.table.ChecklistEntity
import com.jksol.keep.notes.data.database.table.ChecklistItemEntity
import com.jksol.keep.notes.data.database.table.TextNoteEntity

@Database(
    entities = [
        TextNoteEntity::class,
        ChecklistEntity::class,
        ChecklistItemEntity::class,
    ], version = 5, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun textNoteDao(): TextNoteDao

    abstract fun checklistDao(): ChecklistDao

    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        const val DB_NAME = "application_database"
    }
}