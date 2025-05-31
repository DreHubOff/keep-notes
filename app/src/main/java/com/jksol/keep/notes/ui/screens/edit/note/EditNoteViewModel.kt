package com.jksol.keep.notes.ui.screens.edit.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val buildModificationDateText: BuildModificationDateTextInteractor,
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
) : ViewModel() {

    private val initialNoteId = savedStateHandle.toRoute<Route.EditNoteScreen>().noteId ?: 0

    private val _state = MutableStateFlow(EditNoteScreenState.EMPTY)
    val state: Flow<EditNoteScreenState> = _state
        .asStateFlow()
        .onStart { loadInitialState() }

    private var titleModificationJob: Job? = null
    private var contentModificationJob: Job? = null

    fun onTitleChanged(title: String) {
        titleModificationJob?.cancel()
        titleModificationJob = viewModelScope.launch {
            contentModificationJob?.join()
            delay(600)
            val currentState = _state.value
            updateNote(state = currentState, title = title)
        }
    }

    fun onContentChanged(content: String) {
        contentModificationJob?.cancel()
        contentModificationJob = viewModelScope.launch {
            titleModificationJob?.join()
            delay(600)
            val currentState = _state.value
            updateNote(state = currentState, content = content)
        }
    }

    fun onBackClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            contentModificationJob?.join()
            titleModificationJob?.join()
            val currentState = _state.value
            navigationEventsHost.navigateBack(
                Route.EditNoteScreen.Result.KEY to Route.EditNoteScreen.Result(currentState.noteId)
            )
        }
    }

    fun onPinCheckedChange(pinned: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            updateNote(state = currentState, isPinned = pinned)
        }
    }

    fun onTitleNextClick() {
        _state.update { state -> state.copy(contentFocusRequest = ElementFocusRequest()) }
    }

    private fun loadInitialState() {
        viewModelScope.launch(Dispatchers.Default) {
            var currentNote = textNotesRepository.getNoteById(initialNoteId)
            var requestContentFocus = false
            if (currentNote == null) {
                currentNote = textNotesRepository.saveTextNote(TextNote.generateEmpty())
                requestContentFocus = true
            }
            _state.update {
                it.copy(
                    noteId = currentNote.id,
                    title = currentNote.title,
                    content = currentNote.content,
                    modificationStatusMessage = buildModificationDateText(currentNote.modificationDate),
                    isPinned = currentNote.isPinned,
                    contentFocusRequest = if (requestContentFocus) ElementFocusRequest() else null
                )
            }
        }
    }

    private suspend fun updateNote(
        state: EditNoteScreenState,
        title: String = state.title,
        content: String = state.content,
        isPinned: Boolean = state.isPinned,
    ) {
        val newDate = OffsetDateTime.now()
        textNotesRepository.updateNoteContent(
            noteId = state.noteId,
            updateTime = newDate,
            title = title,
            content = content,
            isPinned = isPinned
        )
        val newState = state.copy(
            title = title,
            content = content,
            modificationStatusMessage = buildModificationDateText(newDate),
            isPinned = isPinned
        )
        _state.emit(newState)
    }
}