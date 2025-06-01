package com.jksol.keep.notes.ui.screens.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.core.interactor.ObserveApplicationMainTypeTrashedInteractor
import com.jksol.keep.notes.core.interactor.PermanentlyDeleteApplicationMainDataTypeInteractor
import com.jksol.keep.notes.core.interactor.PermanentlyDeleteOldTrashRecordsInteractor
import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.Route
import com.jksol.keep.notes.ui.screens.trash.mapper.ApplicationMainDataTypeToTrashListItemMapper
import com.jksol.keep.notes.ui.screens.trash.model.TrashScreenState
import com.jksol.keep.notes.ui.screens.trash.model.UiIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val navigationEventsHost: NavigationEventsHost,
    private val observeApplicationMainTypeTrashed: ObserveApplicationMainTypeTrashedInteractor,
    private val permanentlyDeleteApplicationMainDataType: PermanentlyDeleteApplicationMainDataTypeInteractor,
    private val permanentlyDeleteOldTrashRecords: PermanentlyDeleteOldTrashRecordsInteractor,
    private val mainDataTypeToTrashListItemMapper: ApplicationMainDataTypeToTrashListItemMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(TrashScreenState.EMPTY)
    val state: Flow<TrashScreenState> = _state
        .asStateFlow()
        .onStart { observeTrashedItems() }

    private var dbSubscriptionJob: Job? = null

    fun handleEvent(intent: UiIntent) {
        when (intent) {
            UiIntent.BackClicked -> onBackClicked()
            UiIntent.EmptyTrash -> onEmptyTrashRequested()
            is UiIntent.OpenChecklistScreen -> onOpenChecklistScreen(intent)
            is UiIntent.OpenTextNoteScreen -> onOpenTextNoteScreen(intent)
            UiIntent.DismissEmptyTrashConfirmation -> onDismissEmptyTrashConfirmation()
            UiIntent.EmptyTrashConfirmed -> onEmptyTrashConfirmed()
        }
    }

    private fun onOpenTextNoteScreen(intent: UiIntent.OpenTextNoteScreen) {
        viewModelScope.launch {
            navigationEventsHost.navigate(Route.EditNoteScreen(noteId = intent.item.id))
        }
    }

    private fun onOpenChecklistScreen(intent: UiIntent.OpenChecklistScreen) {
        viewModelScope.launch {
            navigationEventsHost.navigate(Route.EditChecklistScreen(checklistId = intent.item.id))
        }
    }

    private fun onEmptyTrashRequested() {
        _state.update { it.copy(requestEmptyTrashConfirmation = true) }
    }

    private fun onDismissEmptyTrashConfirmation() {
        _state.update { it.copy(requestEmptyTrashConfirmation = false) }
    }

    private fun onEmptyTrashConfirmed() {
        _state.update {
            it.copy(
                requestEmptyTrashConfirmation = false,
                listItems = emptyList()
            )
        }
        viewModelScope.launch(Dispatchers.Default) {
            val itemsToRemove = observeApplicationMainTypeTrashed().first()
            permanentlyDeleteApplicationMainDataType(*itemsToRemove.toTypedArray())
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            onDismissEmptyTrashConfirmation()
            navigationEventsHost.navigateBack()
        }
    }

    private fun observeTrashedItems() {
        dbSubscriptionJob?.cancel()
        dbSubscriptionJob = viewModelScope.launch(Dispatchers.Default) {
            supervisorScope {
                launch {
                    permanentlyDeleteOldTrashRecords()
                }
                launch {
                    observeApplicationMainTypeTrashed()
                        .collect { trashedItem: List<ApplicationMainDataType> ->
                            val newScreenItems = trashedItem.map(mainDataTypeToTrashListItemMapper::invoke)
                            _state.update { it.copy(listItems = newScreenItems) }
                        }
                }
            }
        }
    }
}