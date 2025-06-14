package com.jksol.keep.notes.ui.screens.edit.checklist

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.ChecklistEditorFacade
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.interactor.BuildPdfFromChecklistInteractor
import com.jksol.keep.notes.core.interactor.BuildTextFromChecklistInteractor
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.ChecklistItem
import com.jksol.keep.notes.core.model.MainTypeTextRepresentation
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.PermissionsRepository
import com.jksol.keep.notes.di.qualifier.ApplicationGlobalScope
import com.jksol.keep.notes.ui.focus.ElementFocusRequest
import com.jksol.keep.notes.ui.intent.ShareFileIntentBuilder
import com.jksol.keep.notes.ui.intent.ShareTextIntentBuilder
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.EditChecklistScreenState
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.core.EditScreenViewModel
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import com.jksol.keep.notes.util.moveItem
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class EditChecklistViewModel @Inject constructor(
    buildModificationDateText: Lazy<BuildModificationDateTextInteractor>,
    shareTextIntentBuilder: Provider<ShareTextIntentBuilder>,
    shareFileIntentBuilder: Provider<ShareFileIntentBuilder>,
    editorFacade: ChecklistEditorFacade,
    permissionsRepository: Provider<PermissionsRepository>,
    private val navigationStateHandle: SavedStateHandle,
    @ApplicationContext
    private val context: Context,
    @ApplicationGlobalScope
    private val applicationCoroutineScope: CoroutineScope,
    private val buildPdfFromChecklist: Provider<BuildPdfFromChecklistInteractor>,
    private val buildTextFromChecklist: Provider<BuildTextFromChecklistInteractor>,
    private val navigationEventsHost: NavigationEventsHost,
    private val checklistRepository: ChecklistRepository,
) : EditScreenViewModel<EditChecklistScreenState, Checklist>(
    navigationEventsHost = navigationEventsHost,
    editorFacade = editorFacade,
    applicationCoroutineScope = applicationCoroutineScope,
    context = context,
    buildModificationDateText = buildModificationDateText,
    shareTextIntentBuilder = shareTextIntentBuilder,
    shareFileIntentBuilder = shareFileIntentBuilder,
    permissionsRepository = permissionsRepository,
) {

    override val itemRestoredMessageRes: Int = R.string.checklist_restored

    override suspend fun loadFirstItem(itemIdFromNavArgs: Long): Checklist {
        var checklist = checklistRepository.getChecklistById(itemIdFromNavArgs)
        if (checklist == null) {
            delay(defaultTransitionAnimationDuration.toLong())
            focusedItemIndex.set(0)
            lastFocusRequest = ElementFocusRequest()
            checklist = checklistRepository.insertChecklist(Checklist.generateEmpty())
        }
        return checklist
    }

    override fun getCurrentIdFromNavigationArgs(): Long =
        navigationStateHandle.toRoute<Route.EditChecklistScreen>().checklistId ?: -1

    override fun itemUpdatesFlow(itemId: Long): Flow<Checklist> {
        return checklistRepository
            .observeChecklistById(itemId)
            .onEach {
                val currentState = _state.value
                if (currentState.uncheckedItems.getOrNull(focusedItemIndex.get())?.id == 0L) {
                    lastFocusRequest = ElementFocusRequest()
                }
            }
    }

    override fun getEmptyState(): EditChecklistScreenState = EditChecklistScreenState.EMPTY

    override fun navigateBack(itemId: Long, isTrashed: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = if (isTrashed) {
                Route.EditChecklistScreen.Result.Trashed(checklistId = itemId)
            } else {
                Route.EditChecklistScreen.Result.Edited(checklistId = itemId)
            }
            navigationEventsHost.navigateBack(Route.EditChecklistScreen.Result.KEY to result)
        }
    }

    override suspend fun getPdfRepresentation(state: EditChecklistScreenState): File? =
        buildPdfFromChecklist.get().invoke(state.itemId)

    override suspend fun getTextRepresentation(state: EditChecklistScreenState): MainTypeTextRepresentation? =
        buildTextFromChecklist.get().invoke(state.itemId)

    override fun fillWithScreenSpecificData(
        oldState: EditChecklistScreenState,
        newState: EditChecklistScreenState,
        updatedItem: Checklist,
    ): EditChecklistScreenState {
        val uncheckedItems = updatedItem
            .items
            .toUncheckedListItemsUi(
                focusedItemIndex = focusedItemIndex.get(),
                focusRequest = lastFocusRequest,
            )
        val checkedItems = updatedItem.items.toCheckedListItemsUi()
        return newState.copy(
            uncheckedItems = uncheckedItems,
            checkedItems = checkedItems,
            showCheckedItems = oldState.showCheckedItems
        )
    }

    private var lastFocusRequest: ElementFocusRequest? = null
    private val focusedItemIndex = AtomicInteger(-1)

    private var itemTitleUpdateJobs: MutableMap<Long, Job> = mutableMapOf()

    fun onBackClick() {
        val checklistId = _state.value.itemId
        navigateBack(itemId = checklistId, isTrashed = false)
    }

    fun onAddChecklistItemClick() {
        applicationCoroutineScope.launch {
            val currentState = _state.value
            val currentList = currentState.uncheckedItems.map { it.copy(focusRequest = null) }
            val blankItem = ChecklistItem.generateEmpty()
            val updatedState = _state.updateAndGet {
                it.copy(uncheckedItems = currentList + blankItem.toUncheckedListItemUi())
            }
            focusedItemIndex.set(updatedState.uncheckedItems.lastIndex)
            lastFocusRequest = ElementFocusRequest()
            checklistRepository.saveChecklistItemAsLast(
                checklistId = currentState.itemId,
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
        applicationCoroutineScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            _state.update { currentState.copy(checkedItems = currentState.checkedItems - item) }
            checklistRepository.updateChecklistItemCheckedState(
                isChecked = false,
                itemId = item.id,
                checklistId = currentState.itemId,
            )
        }
    }

    fun onItemChecked(item: UncheckedListItemUi) {
        applicationCoroutineScope.launch(Dispatchers.Default) {
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
                checklistId = currentState.itemId,
            )
        }
    }

    fun onItemTextChanged(text: String, item: UncheckedListItemUi) {
        itemTitleUpdateJobs[item.id]?.cancel()
        val newJob = applicationCoroutineScope.launch(Dispatchers.Default) {
            delay(600)
            val currentState = _state.updateAndGet { updateItemText(state = it, itemId = item.id, text = text) }
            checklistRepository.updateChecklistItemTitle(
                itemId = item.id,
                checklistId = currentState.itemId,
                title = text,
            )
        }
        newJob.invokeOnCompletion { itemTitleUpdateJobs.remove(item.id) }
        itemTitleUpdateJobs[item.id] = newJob
    }

    fun onDoneClicked(item: UncheckedListItemUi) {
        applicationCoroutineScope.launch(Dispatchers.Default) {
            val oldState = _state.getAndUpdate { insertItemAfterFocused(state = it, focusedItem = item) }
            val indexOfFocused = oldState.uncheckedItems.indexOf(item) + 1
            val currentState = _state.value
            focusedItemIndex.set(indexOfFocused)
            lastFocusRequest = ElementFocusRequest()
            checklistRepository.insertChecklistItemAfterFollowing(
                checklistId = currentState.itemId,
                itemBefore = item.id,
                itemToInsert = ChecklistItem.generateEmpty(),
            )
        }
    }

    fun onDeleteClick(item: UncheckedListItemUi) {
        applicationCoroutineScope.launch(Dispatchers.Default) {
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
            checklistRepository.deleteChecklistItem(itemId = item.id, checklistId = currentState.itemId)
        }
    }

    fun onMoveItems(fromIndex: Int, toIndex: Int) {
        _state.update { initialState ->
            moveUncheckedItems(state = initialState, fromIndex = fromIndex, toIndex = toIndex)
        }
    }

    fun onMoveCompleted() {
        applicationCoroutineScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val uncheckedItems = currentState.uncheckedItems
            checklistRepository.saveItemsNewOrder(
                checklistId = currentState.itemId,
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