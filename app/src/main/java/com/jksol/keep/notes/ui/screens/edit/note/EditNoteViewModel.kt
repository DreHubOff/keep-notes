package com.jksol.keep.notes.ui.screens.edit.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
) : ViewModel() {

    private val initialNoteId = savedStateHandle.toRoute<Route.EditNoteScreen>().noteId ?: 0

    private val _state = MutableStateFlow<EditNoteScreenState>(EditNoteScreenState.None)
    val state: StateFlow<EditNoteScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            var currentNote = textNotesRepository.getNoteById(initialNoteId)
            if (currentNote == null) {
                currentNote = textNotesRepository.saveTextNote(TextNote.generateEmpty())
            }
            _state.emit(
                EditNoteScreenState.Idle(
                    noteId = currentNote.id,
                    title = currentNote.title,
                    content = currentNote.content,
                    modificationStatusMessage = "",
                    reminderTime = "",
                    isPinned = currentNote.isPinned
                )
            )
        }
    }

    private var titleModificationJob: Job? = null
    private var contentModificationJob: Job? = null

    fun onTitleChanged(title: String) {
        titleModificationJob?.cancel()
        titleModificationJob = viewModelScope.launch {
            contentModificationJob?.join()
            delay(600)
            val currentState = state.value
            if (currentState is EditNoteScreenState.Idle) {
                textNotesRepository.updateTitle(currentState.noteId, title)
                _state.emit(currentState.copy(title = title))
            }
        }
    }

    fun onContentChanged(content: String) {
        contentModificationJob?.cancel()
        contentModificationJob = viewModelScope.launch {
            titleModificationJob?.join()
            delay(600)
            val currentState = state.value
            if (currentState is EditNoteScreenState.Idle) {
                textNotesRepository.updateContent(currentState.noteId, content)
                _state.emit(currentState.copy(content = content))
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            navigationEventsHost.navigateBack()
        }
    }

    fun onPinCheckedChange(pinned: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = state.value
            if (currentState is EditNoteScreenState.Idle) {
                textNotesRepository.updatePinnedState(currentState.noteId, pinned)
                _state.emit(currentState.copy(isPinned = pinned))
            }
        }
    }
}