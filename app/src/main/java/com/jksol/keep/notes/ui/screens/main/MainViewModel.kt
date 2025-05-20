package com.jksol.keep.notes.ui.screens.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.data.preferences.UserPreferences
import com.jksol.keep.notes.ui.mapper.toMainScreenItem
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.screens.main.model.StateWithList
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private var noteEditingResult: Route.EditNoteScreen.Result? = null

    private val _uiState = MutableStateFlow<MainScreenState>(MainScreenState.None())
    val uiState: Flow<MainScreenState> = _uiState
        .asStateFlow()
        .onStart { loadIdleState() }

    private var searchJob: Job? = null

    private fun loadIdleState() {
        viewModelScope.launch(Dispatchers.Default) {
            val snackbarEvent = validateNoteEditingResult()
            val isSavedAnyNoteDeferred = async { userPreferences.isSavedAnyNote() }
            val notesDeferred = async { loadNotes() }
            val isSavedAnyNote = isSavedAnyNoteDeferred.await()
            val notes = notesDeferred.await()
            val state = if (notes.isEmpty() && !isSavedAnyNote) {
                MainScreenState.WelcomeBanner(
                    MainScreenItem.TextNote(
                        id = 0,
                        title = context.getString(R.string.welcome_banner_title),
                        content = context.getString(R.string.welcome_banner_content),
                        interactive = false,
                    )
                )
            } else {
                userPreferences.updateSavedAnyNoteState(isSaved = true)
                MainScreenState.Idle(notes)
            }.withSnackbarEvent(snackbarEvent)
            _uiState.emit(state)
        }
    }

    fun openTextNoteEditor(note: MainScreenItem.TextNote?) {
        viewModelScope.launch {
            exitAddModeSelection()
            navigationEventsHost.navigate(
                Route.EditNoteScreen(noteId = note?.id)
            )
        }
    }

    fun openCheckListEditor() {
        viewModelScope.launch {
            exitAddModeSelection()
            navigationEventsHost.navigate(
                Route.EditChecklistScreen(checklistId = null)
            )
        }
    }

    fun toggleAddModeSelection() {
        viewModelScope.launch {
            when (val state = _uiState.value) {
                is MainScreenState.AddModeSelection -> exitAddModeSelection()
                else -> _uiState.emit(MainScreenState.AddModeSelection(state))
            }
        }
    }

    fun onToggleSearchVisibility() {
        viewModelScope.launch {
            when (val state = _uiState.value) {
                is MainScreenState.Search,
                    -> loadIdleState()

                is StateWithList -> _uiState.emit(MainScreenState.Search(state.screenItems))
                is MainScreenState.WelcomeBanner -> _uiState.emit(MainScreenState.Search(emptyList()))
                else -> return@launch
            }
        }
    }

    fun onNewSearchPrompt(searchPrompt: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.Default) {
            val allNotes = loadNotes()
            if (searchPrompt.isEmpty() || searchPrompt.isBlank()) {
                _uiState.emit(MainScreenState.Search(allNotes))
                return@launch
            }

            allNotes.filter { it.title.contains(searchPrompt, ignoreCase = true) }.let {
                _uiState.emit(MainScreenState.Search(it))
            }
        }
    }

    fun saveNoteEditingResult(result: Route.EditNoteScreen.Result?) {
        noteEditingResult = result
    }

    private suspend fun validateNoteEditingResult(): SnackbarEvent? {
        val result = noteEditingResult ?: return null
        val createdNote = textNotesRepository.getNoteById(result.noteId) ?: return null
        val noteIsEmpty = createdNote.title.isEmpty() && createdNote.content.isEmpty()
        if (noteIsEmpty) {
            textNotesRepository.delete(createdNote)
            return SnackbarEvent(context.getString(R.string.empty_notes_discarded))
        }
        return null
    }

    private suspend fun exitAddModeSelection() {
        val state = _uiState.value as? MainScreenState.AddModeSelection ?: return
        _uiState.emit(state.previousState)
    }

    private suspend fun loadNotes(): List<MainScreenItem> =
        textNotesRepository.getNotTrashedNotes().map(TextNote::toMainScreenItem)
}