package com.jksol.keep.notes.ui.screens.edit.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
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
    private val navigationEventsHost: NavigationEventsHost,
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteScreenState>(EditNoteScreenState.Idle())
    val state: StateFlow<EditNoteScreenState> = _state.asStateFlow()

    private var titleModificationJob: Job? = null
    private var contentModificationJob: Job? = null

    fun onTitleChanged(title: String) {
        titleModificationJob?.cancel()
        titleModificationJob = viewModelScope.launch {
            contentModificationJob?.join()
            delay(600)
            val currentState = state.value
            if (currentState is EditNoteScreenState.Idle) {
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
                _state.emit(currentState.copy(isPinned = pinned))
            }
        }
    }
}