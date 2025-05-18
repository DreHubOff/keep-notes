package com.jksol.keep.notes.data.database.converter

import androidx.room.TypeConverter
import com.jksol.keep.notes.core.model.NoteColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromNoteColor(color: NoteColor): String = color.name

    @TypeConverter
    fun toNoteColor(name: String): NoteColor = NoteColor.valueOf(name)
}