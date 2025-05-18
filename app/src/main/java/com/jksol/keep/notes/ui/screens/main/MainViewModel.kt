package com.jksol.keep.notes.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.MainScreenDemoData
import com.jksol.keep.notes.ui.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.screens.main.model.StateWithList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val navigationEventsHost: NavigationEventsHost,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenState>(MainScreenState.Idle(emptyList()))
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        _uiState.tryEmit(MainScreenState.Idle(loadNotes()))
    }

    private fun loadIdleState() {
        viewModelScope.launch {
            _uiState.emit(MainScreenState.Idle(loadNotes()))
        }
    }

    fun openTextNoteEditor() {
        viewModelScope.launch {
            exitAddModeSelection()
            navigationEventsHost.navigate(Route.EditNoteScreen())
        }
    }

    fun openCheckListEditor() {
        viewModelScope.launch {
            exitAddModeSelection()
        }
    }

    fun toggleAddModeSelection() {
        viewModelScope.launch {
            when (val state = uiState.value) {
                is MainScreenState.AddModeSelection -> exitAddModeSelection()
                else -> _uiState.emit(MainScreenState.AddModeSelection(state))
            }
        }
    }

    fun onToggleSearchVisibility() {
        viewModelScope.launch {
            when (val state = uiState.value) {
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
        val state = uiState.value as? MainScreenState.AddModeSelection ?: return
        _uiState.emit(state.previousState)
    }

    private fun loadNotes() = MainScreenDemoData.notesList()
}