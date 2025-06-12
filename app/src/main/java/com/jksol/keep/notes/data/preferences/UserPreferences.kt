package com.jksol.keep.notes.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.jksol.keep.notes.core.model.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val STORAGE_NAME = "KeepNotes-Storage"

private const val SAVED_ANY_NOTE_KEY = "saved-any-note"
private const val THEM_PREFERENCE_KEY = "theme-preference"

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

    suspend fun updateTheme(theme: ThemeType) = withContext(Dispatchers.IO) {
        storage.edit(commit = true) { putInt(THEM_PREFERENCE_KEY, theme.ordinal) }
    }

    suspend fun getTheme(): ThemeType = withContext(Dispatchers.IO) {
        val ordinal = storage.getInt(THEM_PREFERENCE_KEY, ThemeType.SYSTEM_DEFAULT.ordinal)
        ThemeType.entries[ordinal]
    }

    fun observeTheme(): Flow<ThemeType> {
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == THEM_PREFERENCE_KEY) {
                    trySend(key)
                }
            }
            storage.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { storage.unregisterOnSharedPreferenceChangeListener(listener) }
        }
            .filter { key -> key == THEM_PREFERENCE_KEY }
            .map { getTheme() }
            .onStart { emit(getTheme()) }
    }
}