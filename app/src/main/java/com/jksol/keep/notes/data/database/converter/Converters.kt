package com.jksol.keep.notes.data.database.converter

import androidx.room.TypeConverter
import com.jksol.keep.notes.core.model.NoteColor
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class Converters {

    @TypeConverter
    fun fromLocalDateTime(value: OffsetDateTime?): Long? {
        return value?.toEpochSecond()
    }

    @TypeConverter
    fun toLocalDateTime(value: Long?): OffsetDateTime? {
        return value?.let {
            OffsetDateTime.ofInstant(
                Instant.ofEpochSecond(it),
                ZoneId.systemDefault()
            )
        }
    }

    @TypeConverter
    fun fromNoteColor(color: NoteColor): String = color.name

    @TypeConverter
    fun toNoteColor(name: String): NoteColor = NoteColor.valueOf(name)
}