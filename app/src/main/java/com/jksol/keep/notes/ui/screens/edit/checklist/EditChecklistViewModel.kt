package com.jksol.keep.notes.ui.screens.edit.checklist

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.di.ApplicationGlobalScope
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.TrashSnackbarAction
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import com.jksol.keep.notes.util.moveItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.getAndUpdate
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
    @ApplicationContext
    private val context: Context,
    @ApplicationGlobalScope
    private val applicationCoroutineScope: CoroutineScope,
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

    private var lastFocusRequest: ElementFocusRequest? = null
    private val focusedItemIndex = AtomicInteger(-1)

    private var changeTitleJob: Job? = null
    private var pinnedStateSaveJob: Job? = null
    private var itemTitleUpdateJobs: MutableMap<Long, Job> = mutableMapOf()

    fun onBackClick() {
        viewModelScope.launch(Dispatchers.Default) {
            val checklistId = _state.value.checklistId
            navigationEventsHost.navigateBack(
                result = Route.EditChecklistScreen.Result.KEY to
                        Route.EditChecklistScreen.Result.Edited(checklistId = checklistId)
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
            focusedItemIndex.set(updatedState.uncheckedItems.lastIndex)
            lastFocusRequest = ElementFocusRequest()
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
        itemTitleUpdateJobs[item.id]?.cancel()
        val newJob = viewModelScope.launch(Dispatchers.Default) {
            delay(600)
            val currentState = _state.updateAndGet { updateItemText(state = it, itemId = item.id, text = text) }
            checklistRepository.updateChecklistItemTitle(
                itemId = item.id,
                checklistId = currentState.checklistId,
                title = text,
            )
        }
        newJob.invokeOnCompletion { itemTitleUpdateJobs.remove(item.id) }
        itemTitleUpdateJobs[item.id] = newJob
    }

    fun onDoneClicked(item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val oldState = _state.getAndUpdate { insertItemAfterFocused(state = it, focusedItem = item) }
            val indexOfFocused = oldState.uncheckedItems.indexOf(item) + 1
            val currentState = _state.value
            focusedItemIndex.set(indexOfFocused)
            lastFocusRequest = ElementFocusRequest()
            checklistRepository.insertChecklistItemAfterFollowing(
                checklistId = currentState.checklistId,
                itemBefore = item.id,
                itemToInsert = ChecklistItem.generateEmpty(),
            )
        }
    }

    fun onDeleteClick(item: UncheckedListItemUi) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val indexOfRemovedItem = currentState.uncheckedItems.indexOf(item)
            val indexOfFocusedItem = when {
                currentState.uncheckedItems.size <= 1 -> -1
                indexOfRemovedItem == 0 -> 1
                indexOfRemovedItem == currentState.uncheckedItems.lastIndex -> indexOfRemovedItem - 1
                else -> indexOfRemovedItem + 1
            }
            sendRequestFocusEvent(focusedPosition = indexOfFocusedItem)
            delay(50)
            _state.update { it.copy(uncheckedItems = it.uncheckedItems - item) }
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

    fun onItemFocused(item: UncheckedListItemUi) {
        _state.update { state ->
            state.copy(
                uncheckedItems = state
                    .uncheckedItems
                    .mapIndexed { index, listItem ->
                        if (listItem.id == item.id) {
                            focusedItemIndex.set(index)
                            listItem.copy(focusRequest = ElementFocusRequest().apply { confirmProcessing() })
                        } else {
                            listItem.copy(focusRequest = null)
                        }
                    }
            )
        }
    }

    private fun sendRequestFocusEvent(focusedPosition: Int) {
        focusedItemIndex.set(focusedPosition)
        lastFocusRequest = ElementFocusRequest()
        _state.update { state ->
            val uncheckedItems = state.uncheckedItems.mapIndexed { index, item ->
                if (index == focusedItemIndex.get()) {
                    item.copy(focusRequest = lastFocusRequest)
                } else {
                    item.copy(focusRequest = null)
                }
            }
            state.copy(uncheckedItems = uncheckedItems)
        }
    }

    fun onMoveToTrashClick() {
        val checklistId = _state.value.checklistId
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            checklistRepository.moveToTrash(checklistId = checklistId)
        }
        viewModelScope.launch(Dispatchers.Default) {
            navigationEventsHost.navigateBack(
                result = Route.EditChecklistScreen.Result.KEY to
                        Route.EditChecklistScreen.Result.Trashed(checklistId = checklistId)
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
            checklistRepository.delete(checklistId = _state.value.checklistId)
        }
        viewModelScope.launch { onBackClick() }
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
            checklistRepository.restoreChecklist(checklistId = _state.value.checklistId)
        }
    }

    private fun undoNoteRestoration() {
        viewModelScope.launch {
            _state.update { it.copy(isTrashed = true) }
            checklistRepository.moveToTrash(checklistId = _state.value.checklistId)
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

    private suspend fun loadInitialState(): EditChecklistScreenState {
        var checklist = checklistRepository.getChecklistById(initialChecklistId ?: 0)
        if (checklist == null) {
            delay(defaultTransitionAnimationDuration.toLong())
            focusedItemIndex.set(0)
            lastFocusRequest = ElementFocusRequest()
            checklist = checklistRepository.insertChecklist(Checklist.generateEmpty())
        }
        return checklist.toEditChecklistScreenState(
            focusedItemIndex = focusedItemIndex.get(),
            focusRequest = lastFocusRequest,
            showCheckedItems = false,
            modificationStatusMessage = buildModificationDateText(checklist.modificationDate)
        ).copy(
            snackbarEvent = _state.value.snackbarEvent,
            showPermanentlyDeleteConfirmation = _state.value.showPermanentlyDeleteConfirmation
        )
    }

    private fun observeDatabaseChanges(checklistId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            val stateFlow = checklistRepository
                .observeChecklistById(checklistId)
                .map { checklist ->
                    val currentState = _state.value
                    if (currentState.uncheckedItems.getOrNull(focusedItemIndex.get())?.id == 0L) {
                        lastFocusRequest = ElementFocusRequest()
                    }
                    checklist.toEditChecklistScreenState(
                        focusedItemIndex = focusedItemIndex.get(),
                        focusRequest = lastFocusRequest,
                        showCheckedItems = currentState.showCheckedItems,
                        modificationStatusMessage = buildModificationDateText(checklist.modificationDate)
                    ).copy(
                        snackbarEvent = currentState.snackbarEvent,
                        showPermanentlyDeleteConfirmation = currentState.showPermanentlyDeleteConfirmation,
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