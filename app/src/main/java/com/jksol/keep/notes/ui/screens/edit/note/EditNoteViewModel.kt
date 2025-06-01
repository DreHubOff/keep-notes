package com.jksol.keep.notes.ui.screens.edit.note

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.di.ApplicationGlobalScope
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import com.jksol.keep.notes.ui.screens.edit.note.model.TrashSnackbarAction
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
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
    @ApplicationContext
    private val context: Context,
    @ApplicationGlobalScope
    private val applicationCoroutineScope: CoroutineScope,
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
                Route.EditNoteScreen.Result.KEY to
                        Route.EditNoteScreen.Result.Edited(noteId = currentState.noteId)
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

    fun moveToTrash() {
        val currentState = _state.value
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            textNotesRepository.moveToTrash(currentState.noteId)
        }
        viewModelScope.launch(Dispatchers.Default) {
            navigationEventsHost.navigateBack(
                result = Route.EditNoteScreen.Result.KEY to
                        Route.EditNoteScreen.Result.Trashed(noteId = currentState.noteId)
            )
        }
    }

    fun permanentlyDeleteNoteAskConfirmation() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = true) }
    }

    fun permanentlyDeleteNoteConfirmed() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = false) }
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            textNotesRepository.delete(noteId = _state.value.noteId)
        }
        viewModelScope.launch { onBackClicked() }
    }

    fun permanentlyDeleteNoteDismissed() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = false) }
    }

    fun restoreNote() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isTrashed = false,
                    snackbarEvent = SnackbarEvent(
                        message = context.getString(R.string.note_restored),
                        action = SnackbarEvent.Action(
                            label = context.getString(R.string.undo),
                            key = TrashSnackbarAction.UndoNoteRestoration,
                        ),
                    )
                )
            }
            textNotesRepository.restoreNote(_state.value.noteId)
        }
    }

    private fun undoNoteRestoration() {
        viewModelScope.launch {
            _state.update { it.copy(isTrashed = true) }
            textNotesRepository.moveToTrash(_state.value.noteId)
        }
    }

    fun onAttemptEditTrashed() {
        _state.update {
            it.copy(
                snackbarEvent = SnackbarEvent(
                    message = context.getString(R.string.cannot_edit_in_trash),
                    action = SnackbarEvent.Action(
                        label = context.getString(R.string.restore),
                        key = TrashSnackbarAction.Restore,
                    ),
                )
            )
        }
    }

    fun handleSnackbarAction(action: SnackbarEvent.Action) {
        when (action.key as TrashSnackbarAction) {
            TrashSnackbarAction.Restore -> restoreNote()
            TrashSnackbarAction.UndoNoteRestoration -> undoNoteRestoration()
        }
    }

    private fun loadInitialState() {
        viewModelScope.launch(Dispatchers.Default) {
            var currentNote = textNotesRepository.getNoteById(initialNoteId)
            var requestContentFocus = false
            if (currentNote == null) {
                delay(defaultTransitionAnimationDuration.toLong())
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
                    contentFocusRequest = if (requestContentFocus) ElementFocusRequest() else null,
                    isTrashed = currentNote.isTrashed,
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