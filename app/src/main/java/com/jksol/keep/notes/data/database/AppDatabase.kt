package com.jksol.keep.notes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jksol.keep.notes.data.database.converter.Converters
import com.jksol.keep.notes.data.database.dao.TextNoteDao
import com.jksol.keep.notes.data.database.table.TextNoteEntity


@Database(entities = [TextNoteEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun textNoteDao(): TextNoteDao

    companion object {
        const val DB_NAME = "application_database"
    }
}