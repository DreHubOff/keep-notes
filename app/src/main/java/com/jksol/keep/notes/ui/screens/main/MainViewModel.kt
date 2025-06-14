package com.jksol.keep.notes.ui.screens.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.ChecklistEditorFacade
import com.jksol.keep.notes.core.TextNoteEditorFacade
import com.jksol.keep.notes.core.interactor.ObserveApplicationMainTypeInteractor
import com.jksol.keep.notes.core.interactor.PermanentlyDeleteOldTrashRecordsInteractor
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.core.model.ThemeType
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.TextNotesRepository
import com.jksol.keep.notes.data.preferences.UserPreferences
import com.jksol.keep.notes.di.qualifier.ApplicationGlobalScope
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.main.actionbar.MainActionBarIntent
import com.jksol.keep.notes.ui.screens.main.mapper.toMainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.BackgroundSelectionData
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.screens.main.model.MainSnackbarActionKey
import com.jksol.keep.notes.ui.screens.main.model.ThemeOption
import com.jksol.keep.notes.ui.screens.main.model.ThemeSelectorData
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
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @ApplicationGlobalScope
    private val applicationScope: CoroutineScope,
    private val navigationEventsHost: NavigationEventsHost,
    private val observeApplicationMainType: ObserveApplicationMainTypeInteractor,
    private val permanentlyDeleteOldTrashRecords: PermanentlyDeleteOldTrashRecordsInteractor,
    private val textNotesRepository: TextNotesRepository,
    private val checklistRepository: ChecklistRepository,
    private val textNotesFacade: TextNoteEditorFacade,
    private val checklistFacade: ChecklistEditorFacade,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState.EMPTY)
    val uiState: StateFlow<MainScreenState> = _uiState
        .onStart { observeDatabase(searchPrompt = "") }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(stopTimeoutMillis = 3000),
            initialValue = MainScreenState.EMPTY
        )

    private var searchJob: Job? = null
    private var databaseObserverJob: Job? = null

    private fun observeDatabase(searchPrompt: String) {
        databaseObserverJob?.cancel()
        databaseObserverJob = viewModelScope.launch(Dispatchers.Default) {
            val stateFlow: Flow<MainScreenState> = observeApplicationMainType(searchPrompt.trim())
                .map { items ->
                    val currentItems = _uiState.value.screenItems
                    items.map { item ->
                        val oldItem = currentItems.firstOrNull { old ->
                            (old is MainScreenItem.TextNote && item is TextNote && old.id == item.id) ||
                                    (old is MainScreenItem.Checklist && item is Checklist && old.id == item.id)
                        }
                        item.toMainScreenItem(isSelected = oldItem?.isSelected == true, customBackground = item.backgroundColor)
                    }
                }
                .map { items -> mainScreenStateFromItems(items, searchPrompt) }
            _uiState.emitAll(stateFlow)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            if (_uiState.value.isSelectionMode) {
                onExitSelectionMode()
                return@launch
            }
            if (_uiState.value.searchEnabled) {
                onToggleSearchVisibility()
                return@launch
            }
            navigationEventsHost.navigateBack()
        }
    }

    fun openTextNoteEditor(note: MainScreenItem.TextNote?) {
        viewModelScope.launch {
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

    private fun onToggleSearchVisibility() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _uiState.value
            if (currentState.searchEnabled) {
                exitSearch()
            } else {
                _uiState.update { state -> state.copy(searchEnabled = true, searchPrompt = "") }
            }
        }
    }

    private fun onNewSearchPrompt(searchPrompt: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { state -> state.copy(searchPrompt = searchPrompt, searchEnabled = true) }
            delay(300)
            observeDatabase(searchPrompt = searchPrompt)
        }
    }

    fun processNoteEditingResult(result: Route.EditNoteScreen.Result?) {
        viewModelScope.launch(Dispatchers.Default) {
            delay((defaultTransitionAnimationDuration * 1.5).toLong())
            val resultMessageEvent = validateNoteEditingResult(result ?: return@launch) ?: return@launch
            _uiState.update { state -> state.copy(snackbarEvent = resultMessageEvent) }
        }
    }

    fun processChecklistEditingResult(result: Route.EditChecklistScreen.Result?) {
        viewModelScope.launch(Dispatchers.Default) {
            delay((defaultTransitionAnimationDuration * 1.5).toLong())
            val resultMessageEvent = validateChecklistEditingResult(result ?: return@launch) ?: return@launch
            _uiState.update { state -> state.copy(snackbarEvent = resultMessageEvent) }
        }
    }

    fun handleSnackbarAction(action: SnackbarEvent.Action) {
        viewModelScope.launch {
            when (val actionKey = action.key) {
                is MainSnackbarActionKey.UndoTrashedChecklist ->
                    checklistFacade.restoreItemFromTrash(actionKey.checklistId)

                is MainSnackbarActionKey.UndoTrashedNote ->
                    textNotesFacade.restoreItemFromTrash(actionKey.noteId)

                is MainSnackbarActionKey.UndoTrashedItemList ->
                    undoTrashedItems(actionKey.items)
            }
        }
    }

    private fun undoTrashedItems(items: List<MainScreenItem>) {
        applicationScope.launch {
            supervisorScope {
                items.forEach { item ->
                    when (item) {
                        is MainScreenItem.Checklist -> launch { checklistFacade.restoreItemFromTrash(item.id) }
                        is MainScreenItem.TextNote -> launch { textNotesFacade.restoreItemFromTrash(item.id) }
                    }
                }
            }
        }
    }

    fun openTrashClick() {
        viewModelScope.launch {
            navigationEventsHost.navigate(Route.TrashScreen)
        }
    }

    fun clearTrashOldRecords() {
        viewModelScope.launch(Dispatchers.Default) {
            if (Duration.between(lastTrashClearOperationTime, OffsetDateTime.now()).toMinutes() > 1) {
                permanentlyDeleteOldTrashRecords()
                lastTrashClearOperationTime = OffsetDateTime.now()
            }
        }
    }

    fun onChecklistLongClick(checklist: MainScreenItem.Checklist) {
        changeItemSelectionState(selectedItem = checklist)
    }

    fun onTextNoteLongClick(textNote: MainScreenItem.TextNote) {
        changeItemSelectionState(selectedItem = textNote)
    }

    private fun onExitSelectionMode() {
        _uiState.update { state ->
            val listItems = state.screenItems.map { item ->
                if (item.isSelected) item.withSelection(isSelected = false) else item
            }
            state.copy(screenItems = listItems, selectedItemsArePinned = false)
        }
    }

    private fun onMoveToTrashSelected() {
        applicationScope.launch(Dispatchers.Default) {
            val itemsToTrash = _uiState.value.screenItems.filter { it.isSelected }
            onExitSelectionMode()
            supervisorScope {
                itemsToTrash.forEach { item ->
                    when (item) {
                        is MainScreenItem.Checklist -> launch { checklistFacade.moveToTrash(item.id) }
                        is MainScreenItem.TextNote -> launch { textNotesFacade.moveToTrash(item.id) }
                    }
                }
            }
            notifyItemsMovedToTrash(itemsToTrash)
        }
    }

    private fun notifyItemsMovedToTrash(trashedItems: List<MainScreenItem>) {
        _uiState.update { oldState ->
            val snackbarEvent = SnackbarEvent(
                message = context.getString(R.string.note_moved_to_trash),
                action = SnackbarEvent.Action(
                    label = context.getString(R.string.undo),
                    key = MainSnackbarActionKey.UndoTrashedItemList(items = trashedItems)
                )
            )
            oldState.copy(snackbarEvent = snackbarEvent)
        }
    }

    private fun onPinnedStateChangedForSelected(checked: Boolean) {
        applicationScope.launch(Dispatchers.Default) {
            val itemsToUpdate = _uiState.value.screenItems.filter { it.isSelected }
            _uiState.update { it.copy(selectedItemsArePinned = checked) }
            supervisorScope {
                itemsToUpdate.forEach { item ->
                    when (item) {
                        is MainScreenItem.Checklist ->
                            launch { checklistFacade.storePinnedSate(pinned = checked, itemId = item.id) }

                        is MainScreenItem.TextNote ->
                            launch { textNotesFacade.storePinnedSate(pinned = checked, itemId = item.id) }
                    }
                }
            }
            onExitSelectionMode()
        }
    }

    fun handleActionBarEvent(event: MainActionBarIntent) {
        when (event) {
            is MainActionBarIntent.ChangePinnedStateOfSelected -> onPinnedStateChangedForSelected(event.isPinned)
            MainActionBarIntent.HideSearch -> onToggleSearchVisibility()
            MainActionBarIntent.HideSelection -> onExitSelectionMode()
            MainActionBarIntent.MoveToTrashSelected -> onMoveToTrashSelected()
            MainActionBarIntent.OpenSearch -> onToggleSearchVisibility()
            MainActionBarIntent.OpenSideMenu -> openSideMenu()
            is MainActionBarIntent.Search -> onNewSearchPrompt(searchPrompt = event.prompt)
            MainActionBarIntent.SelectBackground -> onPicBackgroundForSelected()
        }
    }

    fun onHideBackgroundSelection() {
        _uiState.update { it.copy(backgroundSelectionData = null) }
    }

    fun applyBackgroundToSelected(color: NoteColor?) {
        onHideBackgroundSelection()
        applicationScope.launch(Dispatchers.Default) {
            val itemsToUpdate = _uiState.value.screenItems.filter { it.isSelected }
            onExitSelectionMode()
            supervisorScope {
                itemsToUpdate.forEach { item ->
                    when (item) {
                        is MainScreenItem.Checklist ->
                            launch { checklistFacade.saveBackgroundColor(itemId = item.id, color = color) }

                        is MainScreenItem.TextNote ->
                            launch { textNotesFacade.saveBackgroundColor(itemId = item.id, color = color) }
                    }
                }
            }
        }
    }

    fun openThemeSelection() {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedThemeType = userPreferences.getTheme()
            val selectorOptions = ThemeType.entries.map { type ->
                val selected = selectedThemeType == type
                when (type) {
                    ThemeType.LIGHT -> ThemeOption(nameRes = R.string.light, type = type, isSelected = selected)
                    ThemeType.DARK -> ThemeOption(nameRes = R.string.dark, type = type, isSelected = selected)
                    ThemeType.SYSTEM_DEFAULT -> ThemeOption(nameRes = R.string.system_default, type = type, isSelected = selected)
                }
            }
            _uiState.update { it.copy(themeSelectorData = ThemeSelectorData(selectorOptions)) }
        }
    }

    fun onHideThemeSelection() {
        _uiState.update { it.copy(themeSelectorData = null) }
    }

    fun applyTheme(themeType: ThemeType) {
        onHideThemeSelection()
        applicationScope.launch {
            userPreferences.updateTheme(themeType)
        }
    }

    private fun onPicBackgroundForSelected() {
        viewModelScope.launch(Dispatchers.Default) {
            val colors: List<NoteColor?> = buildList {
                add(null)
                addAll(NoteColor.entries)
            }

            _uiState.update {
                it.copy(backgroundSelectionData = BackgroundSelectionData(colors = colors, selectedColor = null))
            }
        }
    }

    private fun openSideMenu() {
        _uiState.update { it.copy(openSideMenuEvent = ElementFocusRequest()) }
    }

    private fun <T : MainScreenItem> changeItemSelectionState(selectedItem: T) {
        _uiState.update { state ->
            val listItems = state.screenItems.map { item ->
                if (item.compositeKey == selectedItem.compositeKey) {
                    item.withSelection(isSelected = item.isSelected.not())
                } else {
                    item
                }
            }
            val selectedItemsArePinned = listItems.filter { it.isSelected }.all { it.isPinned }
            state.copy(screenItems = listItems, selectedItemsArePinned = selectedItemsArePinned)
        }
    }

    private suspend fun validateNoteEditingResult(result: Route.EditNoteScreen.Result): SnackbarEvent? {
        return when (result) {
            is Route.EditNoteScreen.Result.Edited -> onTextNoteEdited(result.noteId)
            is Route.EditNoteScreen.Result.Trashed -> onTextNoteTrashed(result.noteId)
        }
    }

    private fun onTextNoteTrashed(noteId: Long): SnackbarEvent {
        return SnackbarEvent(
            message = context.getString(R.string.note_moved_to_trash),
            action = SnackbarEvent.Action(
                label = context.getString(R.string.undo),
                key = MainSnackbarActionKey.UndoTrashedNote(noteId = noteId)
            )
        )
    }

    private suspend fun onTextNoteEdited(noteId: Long): SnackbarEvent? {
        val createdNote = textNotesRepository.getNoteById(noteId) ?: return null
        if (createdNote.isEmpty()) {
            textNotesFacade.permanentlyDelete(createdNote.id)
            return SnackbarEvent(context.getString(R.string.empty_notes_discarded))
        }
        return null
    }

    private suspend fun validateChecklistEditingResult(result: Route.EditChecklistScreen.Result): SnackbarEvent? {
        return when (result) {
            is Route.EditChecklistScreen.Result.Edited -> onChecklistEdited(result.checklistId)
            is Route.EditChecklistScreen.Result.Trashed -> onChecklistTrashed(result.checklistId)
        }
    }

    private fun onChecklistTrashed(checklistId: Long): SnackbarEvent {
        return SnackbarEvent(
            message = context.getString(R.string.checklist_moved_to_trash),
            action = SnackbarEvent.Action(
                label = context.getString(R.string.undo),
                key = MainSnackbarActionKey.UndoTrashedChecklist(checklistId = checklistId)
            )
        )
    }

    private suspend fun onChecklistEdited(checklistId: Long): SnackbarEvent? {
        val createdChecklist = checklistRepository.getChecklistById(checklistId) ?: return null
        if (createdChecklist.isEmpty()) {
            checklistFacade.permanentlyDelete(createdChecklist.id)
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
        val screenItems = if (items.isEmpty() && !savedAnyNote && searchPrompt.isEmpty()) {
            val welcomeBanner = buildWelcomeBanner()
            userPreferences.updateSavedAnyNoteState(isSaved = true)
            textNotesRepository.saveTextNote(
                TextNote
                    .generateEmpty()
                    .copy(title = welcomeBanner.title, content = welcomeBanner.content)
            )
            emptyList()
        } else {
            items
        }
        return currentState.copy(screenItems = screenItems, searchPrompt = searchPrompt)
    }

    private fun requestNavigationOverlay() {
        _uiState.update { it.copy(showNavigationOverlay = ElementFocusRequest()) }
    }

    private fun buildWelcomeBanner(): MainScreenItem.TextNote {
        return MainScreenItem.TextNote(
            id = 0,
            title = context.getString(R.string.welcome_banner_title),
            content = context.getString(R.string.welcome_banner_content),
        )
    }

    companion object {

        var lastTrashClearOperationTime: OffsetDateTime = OffsetDateTime.MIN
    }
}