package com.jksol.keep.notes.core.interactor

import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.SortableListItem
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.TextNotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveApplicationMainTypeInteractor @Inject constructor(
    private val textNotesRepository: TextNotesRepository,
    private val checklistRepository: ChecklistRepository,
) {

    operator fun invoke(searchPrompt: String): Flow<List<ApplicationMainDataType>> {
        return textNotesRepository
            .observeNotTrashedNotes()
            .combine(checklistRepository.observeNotTrashedChecklists()) { textNotes, checklists ->
                val sequence = sequenceOf(textNotes, checklists)
                    .flatten()
                    .sortedByPinnedAndModificationDate()
                if (searchPrompt.trim().isEmpty()) {
                    sequence
                } else {
                    sequence.filter { item ->
                        item.title.contains(searchPrompt, ignoreCase = true) || when (item) {
                            is TextNote -> item.content.contains(searchPrompt, ignoreCase = true)
                            is Checklist -> item.items.any { it.title.contains(searchPrompt, ignoreCase = true) }
                            else -> false
                        }
                    }
                }.toList()
            }
    }

    private fun <T : SortableListItem> Sequence<T>.sortedByPinnedAndModificationDate(): Sequence<T> {
        return this.sortedWith(
            compareByDescending<T> { it.isPinned }.thenByDescending { it.creationDate }
        )
    }
}