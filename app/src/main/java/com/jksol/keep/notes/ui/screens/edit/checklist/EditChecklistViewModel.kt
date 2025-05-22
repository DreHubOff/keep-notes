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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

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
    private var addNewItemJob: Job? = null
    private var itemUncheckedJob: Job? = null
    private var itemCheckedJob: Job? = null
    private var itemTitleUpdateJob: Job? = null
    private var deleteItemJob: Job? = null
    private var moveItemsJob: Job? = null

    fun onBackClick() {
        viewModelScope.launch(Dispatchers.Default) {
            changeTitleJob?.join()
            pinnedStateSaveJob?.join()
            addNewItemJob?.join()
            itemUncheckedJob?.join()
            itemCheckedJob?.join()
            itemTitleUpdateJob?.join()
            deleteItemJob?.join()
            moveItemsJob?.join()
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
            checklistRepository.updateChecklistTitle(checklistId = _state.value.checklistId, title = newTitle)
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
        addNewItemJob = viewModelScope.launch {
            val currentState = _state.value
            val currentList = currentState.uncheckedItems
            val blankItem = ChecklistItem.EMPTY
            val savedItem = checklistRepository.saveChecklistItemAsLast(
                checklistId = currentState.checklistId,
                item = blankItem,
            )
            val uiItem = savedItem.toUncheckedListItemUi()
            notifyChecklistModified(uncheckedItems = currentList + uiItem)
            onFocusStateChanged(isFocused = true, item = uiItem)
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
            checklistRepository.updateChecklistItemCheckedState(
                isChecked = false,
                itemId = item.id,
                checklistId = currentState.checklistId
            )
            val uncheckedItems = checklistRepository.getUncheckedItemsForChecklist(checklistId = currentState.checklistId)
            val checkedItems = checklistRepository.getCheckedItemsForChecklist(checklistId = currentState.checklistId)
            notifyChecklistModified(
                checkedItems = checkedItems.map { it.toCheckedListItemUi() },
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
            val checkedItems = checklistRepository.getCheckedItemsForChecklist(checklistId = currentState.checklistId)
            notifyChecklistModified(
                checkedItems = checkedItems.map { it.toCheckedListItemUi() },
                uncheckedItems = currentState.uncheckedItems - item
            )
        }
    }

    fun onItemTextChanged(text: String, item: UncheckedListItemUi) {
        itemTitleUpdateJob?.cancel()
        itemTitleUpdateJob = viewModelScope.launch(Dispatchers.Default) {
            delay(600)
            val currentState = _state.value
            coroutineScope {
                launch {
                    checklistRepository.updateChecklistItemTitle(
                        itemId = item.id,
                        checklistId = currentState.checklistId,
                        title = text,
                    )
                }
                launch {
                    val updateList = currentState.uncheckedItems.map { updateItem ->
                        if (updateItem.id == item.id) updateItem.copy(text = text) else updateItem
                    }
                    notifyChecklistModified(uncheckedItems = updateList)
                }
            }
        }
    }

    fun onDoneClicked(item: UncheckedListItemUi) {
        addNewItemJob = viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val allItems = checklistRepository.getItemsForChecklist(currentState.checklistId)
            val doneItemIndex = allItems.indexOfFirst { it.id == item.id }
            val stableItems = allItems.subList(0, doneItemIndex + 1)
            var newItem = ChecklistItem.EMPTY.copy(listPosition = allItems[doneItemIndex].listPosition + 1)
            val itemsToUpdate: List<ChecklistItem> = if (doneItemIndex == allItems.lastIndex) {
                emptyList()
            } else {
                allItems
                    .subList(doneItemIndex + 1, allItems.size)
                    .map { it.copy(listPosition = it.listPosition + 2) }
            }
            coroutineScope {
                itemsToUpdate.forEach {
                    launch {
                        checklistRepository.saveChecklistItem(checklistId = currentState.checklistId, it)
                    }
                }
                launch {
                    newItem = checklistRepository.saveChecklistItem(checklistId = currentState.checklistId, item = newItem)
                    val fullList: List<ChecklistItem> = stableItems + newItem + itemsToUpdate
                    notifyChecklistModified(
                        uncheckedItems = fullList.toUncheckedListItemsUi(newItem.id),
                        checkedItems = fullList.toCheckedListItemsUi(),
                    )
                }
            }
        }
    }

    fun onFocusStateChanged(isFocused: Boolean, item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val targetItem = currentState.uncheckedItems.find { it.id == item.id }
            if (targetItem == null || targetItem.isFocused == isFocused) {
                return@launch
            }
            val updateList = currentState.uncheckedItems.map { updateItem ->
                if (updateItem.id == item.id) {
                    updateItem.copy(isFocused = isFocused)
                } else {
                    updateItem.copy(isFocused = false)
                }
            }
            _state.update { it.copy(uncheckedItems = updateList) }
        }
    }

    fun onDeleteClick(item: UncheckedListItemUi) {
        deleteItemJob = viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            coroutineScope {
                launch {
                    checklistRepository.deleteChecklistItem(itemId = item.id, checklistId = currentState.checklistId)
                }
                launch {
                    notifyChecklistModified(uncheckedItems = currentState.uncheckedItems - item)
                }
            }
        }
    }

    fun onMoveItems(fromIndex: Int, toIndex: Int) {
        moveItemsJob = viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val fullList = checklistRepository.getItemsForChecklist(currentState.checklistId)
            val itemToMove = currentState.uncheckedItems[fromIndex]
            val destinationItem = currentState.uncheckedItems[toIndex]

            val fullListFromIndex = fullList.indexOfFirst { it.id == itemToMove.id }
            val fullListToIndex = fullList.indexOfFirst { it.id == destinationItem.id } + 1

            val buffer = fullList.toMutableList()
            buffer.moveItem(fullListFromIndex, fullListToIndex)
            for (i in buffer.indices) {
                buffer[i] = buffer[i].copy(listPosition = i)
            }
            coroutineScope {
                buffer.forEach {
                    launch {
                        checklistRepository.saveChecklistItem(checklistId = currentState.checklistId, it)
                    }
                }
                launch {
                    val focusedItemId = currentState.uncheckedItems.indexOfFirst { it.isFocused }.toLong()
                    notifyChecklistModified(
                        uncheckedItems = buffer.toUncheckedListItemsUi(focusedItemId = focusedItemId),
                        checkedItems = buffer.toCheckedListItemsUi()
                    )
                }
            }
        }
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

    private fun <T> MutableList<T>.moveItem(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        if (fromIndex !in indices || toIndex !in 0..size) return

        val item = removeAt(fromIndex)
        val insertIndex = if (fromIndex < toIndex) toIndex - 1 else toIndex
        add(insertIndex, item)
    }
}