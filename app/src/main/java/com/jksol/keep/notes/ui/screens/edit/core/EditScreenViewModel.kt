package com.jksol.keep.notes.ui.screens.edit.core

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.MainTypeEditorFacade
import com.jksol.keep.notes.core.interactor.BuildModificationDateTextInteractor
import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.core.model.MainTypeTextRepresentation
import com.jksol.keep.notes.di.ApplicationGlobalScope
import com.jksol.keep.notes.ui.intent.ShareFileIntentBuilder
import com.jksol.keep.notes.ui.intent.ShareTextIntentBuilder
import com.jksol.keep.notes.ui.navigation.NavigationEventsHost
import com.jksol.keep.notes.ui.screens.edit.ShareContentType
import com.jksol.keep.notes.ui.shared.SnackbarEvent
import com.jksol.keep.notes.ui.shared.defaultTransitionAnimationDuration
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Provider

abstract class EditScreenViewModel<State : EditScreenState<State>, Item : ApplicationMainDataType>(
    private val navigationEventsHost: NavigationEventsHost,
    private val editorFacade: MainTypeEditorFacade,
    @ApplicationGlobalScope private val applicationCoroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val buildModificationDateText: Lazy<BuildModificationDateTextInteractor>,
    private val shareTextIntentBuilder: Provider<ShareTextIntentBuilder>,
    private val shareFileIntentBuilder: Provider<ShareFileIntentBuilder>,
) : ViewModel() {

    @Suppress("PropertyName")
    protected val _state: MutableStateFlow<State> by lazy { MutableStateFlow(getEmptyState()) }
    val state: Flow<State> by lazy {
        _state
            .asStateFlow()
            .onStart {
                val item = loadFirstItem(getCurrentIdFromNavigationArgs())
                observeEditedItemChanges(item.id)
            }
            .distinctUntilChanged()
    }

    private var pinChangesJob: Job? = null
    private var titleUpdatesJob: Job? = null

    @get:StringRes
    protected abstract val itemRestoredMessageRes: Int

    protected abstract suspend fun loadFirstItem(itemIdFromNavArgs: Long): Item

    protected abstract fun getCurrentIdFromNavigationArgs(): Long

    protected abstract fun fillWithScreenSpesificData(oldState: State, newState: State, updatedItem: Item): State

    protected abstract fun itemUpdatesFlow(itemId: Long): Flow<Item>

    protected abstract fun getEmptyState(): State

    protected abstract fun navigateBack(itemId: Long, isTrashed: Boolean)

    protected abstract suspend fun getTextRepresentation(state: State): MainTypeTextRepresentation?
    protected abstract suspend fun getPdfRepresentation(state: State): File?

    fun onPinCheckedChange(pinned: Boolean) {
        val currentState = _state.updateAndGet { it.copy(isPinned = pinned) }
        pinChangesJob?.cancel()
        pinChangesJob = applicationCoroutineScope.launch {
            editorFacade.storePinnedSate(itemId = currentState.itemId, pinned = pinned)
        }
    }

    fun onTitleChanged(title: String) {
        titleUpdatesJob?.cancel()
        titleUpdatesJob = applicationCoroutineScope.launch {
            delay(600)
            val currentState = _state.updateAndGet { it.copy(title = title) }
            editorFacade.storeNewTitle(itemId = currentState.itemId, title = title)
        }
    }

    fun moveToTrash() {
        val currentState = _state.value
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            editorFacade.moveToTrash(currentState.itemId)
        }
        viewModelScope.launch(Dispatchers.Default) {
            navigateBack(itemId = currentState.itemId, isTrashed = true)
        }
    }

    fun askConfirmationToPermanentlyDeleteItem() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = true) }
    }

    fun dismissPermanentlyDeleteConfirmation() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = false) }
    }

    fun permanentlyDeleteItemWhenConfirmed() {
        val currentState = _state.updateAndGet { it.copy(showPermanentlyDeleteConfirmation = false) }
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            editorFacade.permanentlyDelete(itemId = _state.value.itemId)
        }
        viewModelScope.launch { navigateBack(itemId = currentState.itemId, isTrashed = false) }
    }

    fun restoreItemFromTrash() {
        _state.update { oldState ->
            oldState.copy(
                isTrashed = false,
                snackbarEvent = SnackbarEvent(
                    message = context.getString(itemRestoredMessageRes),
                    action = SnackbarEvent.Action(
                        label = context.getString(R.string.undo),
                        key = TrashSnackbarAction.UndoNoteRestoration,
                    ),
                )
            )
        }
        applicationCoroutineScope.launch {
            editorFacade.restoreItemFromTrash(itemId = _state.value.itemId)
        }
    }

    fun onAttemptEditTrashed() {
        _state.update { oldState ->
            oldState.copy(
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

    open fun handleSnackbarAction(action: SnackbarEvent.Action) {
        when (action.key as TrashSnackbarAction) {
            TrashSnackbarAction.Restore -> restoreItemFromTrash()
            TrashSnackbarAction.UndoNoteRestoration -> undoItemRestoration()
        }
    }

    fun onShareCurrentItemClick() {
        _state.update { it.copy(requestItemShareType = true) }
    }

    fun cancelItemShareTypeRequest() {
        _state.update { it.copy(requestItemShareType = false) }
    }

    fun shareItemAs(shareContentType: ShareContentType) {
        cancelItemShareTypeRequest()
        viewModelScope.launch(Dispatchers.Default) {
            when (shareContentType) {
                ShareContentType.AS_TEXT -> shareAsText()
                ShareContentType.AS_PDF -> shareAsPdf()
            }
        }
    }

    private suspend fun shareAsText() {
        val textRepresentation = getTextRepresentation(_state.value) ?: return
        val shareIntent = shareTextIntentBuilder.get().build(
            subject = textRepresentation.title,
            content = textRepresentation.content,
        ) ?: return
        navigationEventsHost.navigate(intent = shareIntent)
    }

    private suspend fun shareAsPdf() {
        val pdfFile = getPdfRepresentation(_state.value) ?: return
        val shareIntent = shareFileIntentBuilder.get().build(file = pdfFile) ?: return
        navigationEventsHost.navigate(intent = shareIntent)
    }

    private fun undoItemRestoration() {
        _state.update { it.copy(isTrashed = true) }
        applicationCoroutineScope.launch {
            editorFacade.moveToTrash(itemId = _state.value.itemId)
        }
    }

    private fun observeEditedItemChanges(itemId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            itemUpdatesFlow(itemId).collectLatest { updatedItem ->
                _state.update { oldState ->
                    val newState = oldState.copy(
                        itemId = updatedItem.id,
                        title = updatedItem.title,
                        isPinned = updatedItem.isPinned,
                        reminderTime = null,
                        isTrashed = updatedItem.isTrashed,
                        modificationStatusMessage = buildModificationDateText.get().invoke(updatedItem.modificationDate),
                    )
                    fillWithScreenSpesificData(oldState, newState, updatedItem)
                }
            }
        }
    }
}