package com.jksol.keep.notes.ui.screens.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.interactor.ObserveApplicationMainTypeInteractor
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.data.preferences.UserPreferences
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.mapper.toMainScreenItem
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
    private val observeApplicationMainType: ObserveApplicationMainTypeInteractor,
    private val checklistRepository: ChecklistRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState.EMPTY)
    val uiState: Flow<MainScreenState> = _uiState.asStateFlow().onStart { observeDatabase(searchPrompt = "") }

    private var searchJob: Job? = null
    private var databaseObserverJob: Job? = null

    private fun observeDatabase(searchPrompt: String) {
        databaseObserverJob?.cancel()
        databaseObserverJob = viewModelScope.launch(Dispatchers.Default) {
            val stateFlow: Flow<MainScreenState> = observeApplicationMainType(searchPrompt.trim())
                .map { items -> items.map { item -> item.toMainScreenItem() } }
                .map { items -> mainScreenStateFromItems(items, searchPrompt) }
            _uiState.emitAll(stateFlow)
        }
    }

    fun openTextNoteEditor(note: MainScreenItem.TextNote?) {
        viewModelScope.launch {
            exitSearch()
            exitAddModeSelection()
            if (note == null) {
                delay(250)
            }
            requestNavigationOverlay()
            navigationEventsHost.navigate(
                Route.EditNoteScreen(noteId = note?.id)
            )
        }
    }

    fun openCheckListEditor(checklist: MainScreenItem.Checklist?) {
        viewModelScope.launch {
            exitSearch()
            exitAddModeSelection()
            if (checklist == null) {
                delay(250)
            }
            requestNavigationOverlay()
            navigationEventsHost.navigate(
                Route.EditChecklistScreen(checklistId = checklist?.id)
            )
        }
    }

    fun toggleAddModeSelection() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { state ->
                if (state.searchEnabled) {
                    exitSearch()
                }
                state.copy(addItemsMode = !state.addItemsMode)
            }
        }
    }

    fun onToggleSearchVisibility() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _uiState.value
            if (currentState.searchEnabled) {
                exitSearch()
            } else {
                _uiState.update { state -> state.copy(searchEnabled = true, searchPrompt = "") }
            }
        }
    }

    fun onNewSearchPrompt(searchPrompt: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { state -> state.copy(searchPrompt = searchPrompt, searchEnabled = true) }
            delay(500)
            observeDatabase(searchPrompt = searchPrompt)
        }
    }

    fun processNoteEditingResult(result: Route.EditNoteScreen.Result?) {
        viewModelScope.launch(Dispatchers.Default) {
            val resultMessageEvent = validateNoteEditingResult(result ?: return@launch) ?: return@launch
            delay(defaultTransitionAnimationDuration.toLong())
            _uiState.update { state -> state.copy(snackbarEvent = resultMessageEvent) }
        }
    }

    fun processChecklistEditingResult(result: Route.EditChecklistScreen.Result?) {
        viewModelScope.launch(Dispatchers.Default) {
            val resultMessageEvent = validateChecklistEditingResult(result ?: return@launch) ?: return@launch
            delay(defaultTransitionAnimationDuration.toLong())
            _uiState.update { state -> state.copy(snackbarEvent = resultMessageEvent) }
        }
    }

    private suspend fun validateNoteEditingResult(result: Route.EditNoteScreen.Result): SnackbarEvent? {
        val createdNote = textNotesRepository.getNoteById(result.noteId) ?: return null
        val noteIsEmpty = createdNote.title.isEmpty() && createdNote.content.isEmpty()
        if (noteIsEmpty) {
            textNotesRepository.delete(createdNote)
            return SnackbarEvent(context.getString(R.string.empty_notes_discarded))
        }
        return null
    }

    private suspend fun validateChecklistEditingResult(result: Route.EditChecklistScreen.Result): SnackbarEvent? {
        val createdChecklist = checklistRepository.getChecklistById(result.checklistId) ?: return null
        val checklistIsEmpty =
            (createdChecklist.title.isEmpty() && createdChecklist.items.isEmpty()) ||
                    (createdChecklist.title.isEmpty() && createdChecklist.items.sumOf { it.title.trim().length } == 0)
        if (checklistIsEmpty) {
            checklistRepository.delete(createdChecklist)
            return SnackbarEvent(context.getString(R.string.empty_checklist_discarded))
        }
        return null
    }

    private fun exitAddModeSelection() {
        _uiState.update { state -> state.copy(addItemsMode = false) }
    }

    private suspend fun exitSearch() {
        searchJob?.cancel()
        withContext(Dispatchers.Default) {
            _uiState.update { state ->
                val oldSearchPrompt = state.searchPrompt ?: ""
                if (oldSearchPrompt.isNotEmpty()) {
                    observeDatabase(searchPrompt = "")
                }
                state.copy(searchEnabled = false, searchPrompt = null)
            }
        }
    }

    private suspend fun mainScreenStateFromItems(
        items: List<MainScreenItem>,
        searchPrompt: String,
    ): MainScreenState {
        val currentState = _uiState.value
        val savedAnyNote = userPreferences.isSavedAnyNote()
        if (!savedAnyNote && items.isNotEmpty()) {
            userPreferences.updateSavedAnyNoteState(isSaved = true)
        }
        val isWelcomeBanner: Boolean
        val screenItems = if (items.isEmpty() && !savedAnyNote && searchPrompt.isEmpty()) {
            isWelcomeBanner = true
            listOf(buildWelcomeBanner())
        } else {
            isWelcomeBanner = false
            items
        }
        return currentState.copy(screenItems = screenItems, searchPrompt = searchPrompt, isWelcomeBanner = isWelcomeBanner)
    }

    private fun requestNavigationOverlay() {
        _uiState.update { it.copy(showNavigationOverlay = ElementFocusRequest()) }
    }

    private fun buildWelcomeBanner(): MainScreenItem.TextNote {
        return MainScreenItem.TextNote(
            id = 0,
            title = context.getString(R.string.welcome_banner_title),
            content = context.getString(R.string.welcome_banner_content),
            interactive = false,
        )
    }
}