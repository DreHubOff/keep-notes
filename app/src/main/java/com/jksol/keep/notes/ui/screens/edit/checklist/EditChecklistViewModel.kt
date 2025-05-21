package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

private val TAG = EditChecklistViewModel::class.simpleName

@HiltViewModel
class EditChecklistViewModel @Inject constructor(
    navigationStateHandle: SavedStateHandle,
    private val buildModificationDateText: BuildModificationDateTextInteractor,
    private val navigationEventsHost: NavigationEventsHost,
    private val checklistRepository: ChecklistRepository,
) : ViewModel() {

    private val initialChecklistId: Long? = navigationStateHandle.toRoute<Route.EditChecklistScreen>().checklistId

    private val _state = MutableStateFlow(EditChecklistScreenState())
    val state: Flow<EditChecklistScreenState> = _state.onStart {
        loadInitialState()
    }

    private var changeTitleJob: Job? = null
    private var pinnedStateSaveJob: Job? = null
    private var addNewItem: Job? = null
    private var itemUncheckedJob: Job? = null
    private var itemCheckedJob: Job? = null

    fun onBackClick() {
        viewModelScope.launch {
            changeTitleJob?.join()
            pinnedStateSaveJob?.join()
            addNewItem?.join()
            itemUncheckedJob?.join()
            itemCheckedJob?.join()
            val checklistId = _state.value.checklistId
            navigationEventsHost.navigateBack(
                result = Route.EditChecklistScreen.Result.KEY to
                        Route.EditChecklistScreen.Result(checklistId = checklistId)
            )
        }
    }

    fun onTitleChanged(newTitle: String) {
        changeTitleJob?.cancel()
        changeTitleJob = viewModelScope.launch(Dispatchers.Default) {
            delay(600)
            notifyChecklistModified(title = newTitle)
            checklistRepository.updateChecklistTitle(checklistId = initialChecklistId ?: 0, title = newTitle)
        }
    }

    fun onPinCheckedChange(pinned: Boolean) {
        pinnedStateSaveJob?.cancel()
        pinnedStateSaveJob = viewModelScope.launch(Dispatchers.Default) {
            notifyChecklistModified(isPinned = pinned)
            checklistRepository.updatePinnedState(checklistId = _state.value.checklistId, isPinned = pinned)
        }
    }

    fun onAddChecklistItemClick() {
        addNewItem = viewModelScope.launch {
            addNewItem?.join()
            val currentState = _state.value
            val currentList = currentState.uncheckedItems
            val bankItem = ChecklistItem.EMPTY.copy(listPosition = currentList.lastIndex + 1)
            val savedItem = checklistRepository.saveChecklistItem(
                checklistId = currentState.checklistId,
                item = bankItem,
            )
            notifyChecklistModified(uncheckedItems = currentList + savedItem.toUncheckedListItemUi())
        }
    }

    fun toggleCheckedItemsVisibility() {
        viewModelScope.launch {
            _state.update { it.copy(showCheckedItems = !_state.value.showCheckedItems) }
        }
    }

    fun onItemUnchecked(item: CheckedListItemUi) {
        itemUncheckedJob = viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val currentCheckedList = currentState.checkedItems
            checklistRepository.updateChecklistItemCheckedState(
                isChecked = false,
                itemId = item.id,
                checklistId = currentState.checklistId
            )
            val uncheckedItems = checklistRepository.getUncheckedItemsForChecklist(checklistId = currentState.checklistId)
            notifyChecklistModified(
                checkedItems = currentCheckedList - item,
                uncheckedItems = uncheckedItems.map { it.toUncheckedListItemUi() }
            )
        }
    }

    fun onItemChecked(item: UncheckedListItemUi) {
        itemCheckedJob = viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            checklistRepository.updateChecklistItemCheckedState(
                isChecked = true,
                itemId = item.id,
                checklistId = currentState.checklistId
            )
            notifyChecklistModified(
                checkedItems = currentState.checkedItems + item.toCheckedListItemUi(),
                uncheckedItems = currentState.uncheckedItems - item
            )
        }
    }

    fun onItemTextChanged(text: String, item: UncheckedListItemUi) {
        // TODO: update text for the given item
    }

    fun onDoneClicked() {
        // TODO: commit edits or finalize input
    }

    fun onFocusStateChanged(isFocused: Boolean, item: UncheckedListItemUi) {
        // TODO: handle focus changes for the item
    }

    fun onDeleteClick(item: UncheckedListItemUi) {
        // TODO: delete the given checklist item
    }

    private fun loadInitialState() {
        viewModelScope.launch(Dispatchers.Default) {
            var checklist = checklistRepository.getChecklistById(initialChecklistId ?: 0)
            if (checklist == null) {
                checklist = checklistRepository.insertChecklist(Checklist.EMPTY)
            }
            val initialState = checklist.toEditChecklistScreenState(
                focusedItemId = null,
                showCheckedItems = false,
                modificationStatusMessage = buildModificationDateText(checklist.modificationDate)
            )
            _state.emit(initialState)
        }
    }

    private suspend fun notifyChecklistModified(
        title: String = _state.value.title,
        isPinned: Boolean = _state.value.isPinned,
        uncheckedItems: List<UncheckedListItemUi> = _state.value.uncheckedItems,
        checkedItems: List<CheckedListItemUi> = _state.value.checkedItems,
    ) {
        val modificationDate = OffsetDateTime.now()
        checklistRepository.updateChecklistModifiedDate(checklistId = _state.value.checklistId, date = modificationDate)
        _state.update {
            it.copy(
                title = title,
                isPinned = isPinned,
                uncheckedItems = uncheckedItems,
                checkedItems = checkedItems,
                modificationStatusMessage = buildModificationDateText(modificationDate),
            )
        }
    }
}