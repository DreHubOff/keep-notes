package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.util.moveItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class EditChecklistViewModel @Inject constructor(
    navigationStateHandle: SavedStateHandle,
    private val buildModificationDateText: BuildModificationDateTextInteractor,
    private val navigationEventsHost: NavigationEventsHost,
    private val checklistRepository: ChecklistRepository,
) : ViewModel() {

    private val initialChecklistId: Long? = navigationStateHandle.toRoute<Route.EditChecklistScreen>().checklistId

    private val _state = MutableStateFlow(EditChecklistScreenState.EMPTY)
    val state: Flow<EditChecklistScreenState> = _state
        .filter { it !== EditChecklistScreenState.EMPTY }
        .onStart {
            val initialState = loadInitialState()
            emit(initialState)
            observeDatabaseChanges(checklistId = initialState.checklistId)
        }
        .distinctUntilChanged()

    private var focusedItemIndex = AtomicInteger(-1)

    private var changeTitleJob: Job? = null
    private var pinnedStateSaveJob: Job? = null
    private var itemTitleUpdateJob: Job? = null

    fun onBackClick() {
        viewModelScope.launch(Dispatchers.Default) {
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
            _state.update { it.copy(title = newTitle) }
            delay(600)
            checklistRepository.updateChecklistTitle(checklistId = _state.value.checklistId, title = newTitle)
        }
    }

    fun onTitleFocusStateChanged(isFocused: Boolean) {
        if (isFocused) {
            sendRequestFocusEvent(focusedPosition = -1)
        }
    }

    fun onPinCheckedChange(pinned: Boolean) {
        pinnedStateSaveJob?.cancel()
        pinnedStateSaveJob = viewModelScope.launch(Dispatchers.Default) {
            _state.update { it.copy(isPinned = pinned) }
            checklistRepository.updatePinnedState(checklistId = _state.value.checklistId, isPinned = pinned)
        }
    }

    fun onAddChecklistItemClick() {
        viewModelScope.launch {
            val currentState = _state.value
            val currentList = currentState.uncheckedItems.map { it.copy(focusRequest = null) }
            val blankItem = ChecklistItem.generateEmpty()
            val updatedState = _state.updateAndGet {
                it.copy(uncheckedItems = currentList + blankItem.toUncheckedListItemUi())
            }
            sendRequestFocusEvent(focusedPosition = updatedState.uncheckedItems.lastIndex)
            checklistRepository.saveChecklistItemAsLast(
                checklistId = currentState.checklistId,
                item = blankItem,
            )
        }
    }

    fun toggleCheckedItemsVisibility() {
        viewModelScope.launch {
            _state.update { it.copy(showCheckedItems = !_state.value.showCheckedItems) }
        }
    }

    fun onItemUnchecked(item: CheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            _state.update { currentState.copy(checkedItems = currentState.checkedItems - item) }
            checklistRepository.updateChecklistItemCheckedState(
                isChecked = false,
                itemId = item.id,
                checklistId = currentState.checklistId,
            )
        }
    }

    fun onItemChecked(item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val focusedItem = currentState.uncheckedItems.find { it.focusRequest != null }
            val newList = _state
                .updateAndGet { currentState.copy(uncheckedItems = currentState.uncheckedItems - item) }
                .uncheckedItems
            val indexOfFocusedItemInNewList = newList.indexOf(focusedItem)
            sendRequestFocusEvent(indexOfFocusedItemInNewList)
            checklistRepository.updateChecklistItemCheckedState(
                isChecked = true,
                itemId = item.id,
                checklistId = currentState.checklistId,
            )
        }
    }

    fun onItemTextChanged(text: String, item: UncheckedListItemUi) {
        itemTitleUpdateJob?.cancel()
        itemTitleUpdateJob = viewModelScope.launch(Dispatchers.Default) {
            delay(600)
            _state.update { updateItemText(state = it, itemId = item.id, text = text) }
            val currentState = _state.value
            checklistRepository.updateChecklistItemTitle(
                itemId = item.id,
                checklistId = currentState.checklistId,
                title = text,
            )
        }
    }

    fun onDoneClicked(item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            _state.update { insertItemAfterFocused(state = it, focusedItem = item) }
            val currentState = _state.value
            checklistRepository.insertChecklistItemAfterFollowing(
                checklistId = currentState.checklistId,
                itemBefore = item.id,
                itemToInsert = ChecklistItem.generateEmpty(),
            )
        }
    }

    fun onFocusStateChanged(isFocused: Boolean, item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            if (isFocused) {
                focusedItemIndex.set(currentState.uncheckedItems.indexOfFirst { it.id == item.id })
            }
            val newUncheckedItems = currentState.uncheckedItems.mapIndexed { index, item ->
                if (focusedItemIndex.get() != index) {
                    item.copy(focusRequest = null)
                } else {
                    item.copy(focusRequest = ElementFocusRequest().apply { confirmProcessing() })
                }
            }
            _state.update { it.copy(uncheckedItems = newUncheckedItems) }
        }
    }

    fun onDeleteClick(item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val indexOfRemovedItem = currentState.uncheckedItems.indexOf(item)
            val indexOfFocusedItem = when {
                currentState.uncheckedItems.size <= 1 -> -1
                indexOfRemovedItem == 0 -> 0
                indexOfRemovedItem == currentState.uncheckedItems.lastIndex -> indexOfRemovedItem - 1
                else -> indexOfRemovedItem
            }
            _state.update { it.copy(uncheckedItems = it.uncheckedItems - item) }
            sendRequestFocusEvent(focusedPosition = indexOfFocusedItem)
            checklistRepository.deleteChecklistItem(itemId = item.id, checklistId = currentState.checklistId)
        }
    }

    fun onMoveItems(fromIndex: Int, toIndex: Int) {
        _state.update { initialState ->
            moveUncheckedItems(state = initialState, fromIndex = fromIndex, toIndex = toIndex)
        }
    }

    fun onMoveCompleted() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val uncheckedItems = currentState.uncheckedItems
            val indexOfFocused = uncheckedItems.indexOfFirst { it.focusRequest != null }
            sendRequestFocusEvent(focusedPosition = indexOfFocused)

            checklistRepository.saveItemsNewOrder(
                checklistId = currentState.checklistId,
                orderedItemIds = uncheckedItems.map { it.id },
            )
        }
    }

    fun onTitleNextClick() {
        viewModelScope.launch(Dispatchers.Default) {
            val uncheckedItems = _state.value.uncheckedItems
            val hasUncheckedItems = uncheckedItems.isNotEmpty()
            if (hasUncheckedItems) {
                sendRequestFocusEvent(focusedPosition = 0)
            } else {
                onAddChecklistItemClick()
            }
        }
    }

    private fun sendRequestFocusEvent(focusedPosition: Int) {
        focusedItemIndex.set(focusedPosition)
        _state.update { state ->
            val uncheckedItems = state.uncheckedItems.mapIndexed { index, item ->
                if (index == focusedItemIndex.get()) {
                    item.copy(focusRequest = ElementFocusRequest())
                } else {
                    item.copy(focusRequest = null)
                }
            }
            state.copy(uncheckedItems = uncheckedItems)
        }
    }

    private suspend fun loadInitialState(): EditChecklistScreenState {
        var checklist = checklistRepository.getChecklistById(initialChecklistId ?: 0)
        if (checklist == null) {
            checklist = checklistRepository.insertChecklist(Checklist.generateEmpty())
        }
        return checklist.toEditChecklistScreenState(
            focusedItemIndex = null,
            showCheckedItems = false,
            modificationStatusMessage = buildModificationDateText(checklist.modificationDate)
        )
    }

    private fun observeDatabaseChanges(checklistId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            val stateFlow = checklistRepository
                .observeChecklistById(checklistId)
                .map { checklist ->
                    val currentStatus = _state.value
                    checklist.toEditChecklistScreenState(
                        focusedItemIndex = focusedItemIndex.get(),
                        showCheckedItems = currentStatus.showCheckedItems,
                        modificationStatusMessage = buildModificationDateText(checklist.modificationDate)
                    )
                }
            _state.emitAll(stateFlow)
        }
    }

    private fun updateItemText(
        state: EditChecklistScreenState,
        itemId: Long,
        text: String,
    ): EditChecklistScreenState {
        val uncheckedItems = state.uncheckedItems.map { item ->
            if (item.id == itemId) {
                item.copy(text = text)
            } else {
                item
            }
        }
        return state.copy(uncheckedItems = uncheckedItems)
    }

    private fun insertItemAfterFocused(
        state: EditChecklistScreenState,
        focusedItem: UncheckedListItemUi,
    ): EditChecklistScreenState {
        val uncheckedItems = state.uncheckedItems
        val newItemIndex = uncheckedItems.indexOf(focusedItem) + 1
        focusedItemIndex.set(newItemIndex)

        val listAfterFocusedItem = if (newItemIndex == uncheckedItems.size) {
            emptyList()
        } else {
            uncheckedItems.subList(newItemIndex, uncheckedItems.size)
        }
        val newItem = ChecklistItem.generateEmpty().toUncheckedListItemUi()
        val newListOfUnchecked = uncheckedItems.subList(0, newItemIndex) + newItem + listAfterFocusedItem
        return state.copy(uncheckedItems = newListOfUnchecked)
    }

    private fun moveUncheckedItems(
        state: EditChecklistScreenState,
        fromIndex: Int,
        toIndex: Int,
    ): EditChecklistScreenState {
        val uncheckedItems = state.uncheckedItems.toMutableList()
        uncheckedItems.moveItem(fromIndex = fromIndex, toIndex = toIndex)
        return state.copy(uncheckedItems = uncheckedItems.toList())
    }
}