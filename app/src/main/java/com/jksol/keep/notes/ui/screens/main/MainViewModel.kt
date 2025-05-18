package com.jksol.keep.notes.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.ui.mapper.toMainScreenItem
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.screens.main.model.StateWithList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenState>(MainScreenState.Idle(emptyList()))
    val uiState: Flow<MainScreenState> = _uiState.asStateFlow().onStart {
        loadIdleState()
    }

    private var searchJob: Job? = null

    private fun loadIdleState() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.emit(MainScreenState.Idle(loadNotes()))
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

    private suspend fun exitAddModeSelection() {
        val state = _uiState.value as? MainScreenState.AddModeSelection ?: return
        _uiState.emit(state.previousState)
    }

    private suspend fun loadNotes(): List<MainScreenItem> =
        textNotesRepository.getNotTrashedNotes().map(TextNote::toMainScreenItem)
}