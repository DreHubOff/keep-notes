package com.jksol.keep.notes.ui.screens.edit.note

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.note.model.EditNoteScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext
    private val context: Context,
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
) : ViewModel() {

    private val initialNoteId = savedStateHandle.toRoute<Route.EditNoteScreen>().noteId ?: 0
    private val timeFormat by lazy { DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()) }
    private val dateFormat by lazy { DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()) }

    private val _state = MutableStateFlow<EditNoteScreenState>(EditNoteScreenState.None)
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
            if (currentState is EditNoteScreenState.Idle) {
                updateIdleState(state = currentState, title = title)
            }
        }
    }

    fun onContentChanged(content: String) {
        contentModificationJob?.cancel()
        contentModificationJob = viewModelScope.launch {
            titleModificationJob?.join()
            delay(600)
            val currentState = _state.value
            if (currentState is EditNoteScreenState.Idle) {
                updateIdleState(state = currentState, content = content)
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            contentModificationJob?.join()
            titleModificationJob?.join()
            val currentState = _state.value
            if (currentState !is EditNoteScreenState.Idle) return@launch
            navigationEventsHost.navigateBack(
                Route.EditNoteScreen.Result.KEY to Route.EditNoteScreen.Result(currentState.noteId)
            )
        }
    }

    fun onPinCheckedChange(pinned: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            if (currentState is EditNoteScreenState.Idle) {
                updateIdleState(state = currentState, isPinned = pinned)
            }
        }
    }

    private fun buildModificationStatusMessage(modificationDate: OffsetDateTime): String {
        val currentTime = OffsetDateTime.now()
        val duration = Duration.between(modificationDate, currentTime)
        return when {
            duration.toDays() >= 1L -> {
                val time = modificationDate.toLocalDate().format(dateFormat)
                context.getString(R.string.edited_pattern).format(time)
            }

            duration.toMinutes() >= 1L -> {
                val time = modificationDate.toLocalTime().format(timeFormat)
                context.getString(R.string.edited_pattern).format(time).lowercase()
            }

            else -> context.getString(R.string.edited_just_now)
        }
    }

    private fun loadInitialState() {
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
                    modificationStatusMessage = buildModificationStatusMessage(currentNote.modificationDate),
                    reminderTime = "",
                    isPinned = currentNote.isPinned
                )
            )
        }
    }

    private suspend fun updateIdleState(
        state: EditNoteScreenState.Idle,
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
            modificationStatusMessage = buildModificationStatusMessage(newDate),
            isPinned = isPinned
        )
        _state.emit(newState)
    }
}