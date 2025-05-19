package com.jksol.keep.notes.data.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val STORAGE_NAME = "KeepNotes-Storage"

private const val SAVED_ANY_NOTE_KEY = "saved-any-note"

class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val storage by lazy { context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE) }

    suspend fun isSavedAnyNote(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean(SAVED_ANY_NOTE_KEY, false)
    }

    suspend fun updateSavedAnyNoteState(isSaved: Boolean) = withContext(Dispatchers.IO) {
        storage.edit(commit = true) { putBoolean(SAVED_ANY_NOTE_KEY, isSaved) }
    }
}