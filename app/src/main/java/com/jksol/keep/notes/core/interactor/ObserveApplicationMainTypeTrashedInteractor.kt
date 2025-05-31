package com.jksol.keep.notes.core.interactor

import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.core.model.SortableListItem
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.TextNotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveApplicationMainTypeTrashedInteractor @Inject constructor(
    private val textNotesRepository: TextNotesRepository,
    private val checklistRepository: ChecklistRepository,
) {

    operator fun invoke(): Flow<List<ApplicationMainDataType>> {
        return combine(
            flow = textNotesRepository.observeTrashedNotes(),
            flow2 = checklistRepository.observeTrashedChecklists()
        ) { textNotes, checklists ->
            sequenceOf(textNotes, checklists)
                .flatten()
                .sortedByModificationDate()
                .toList()
        }
    }

    private fun <T : SortableListItem> Sequence<T>.sortedByModificationDate(): Sequence<T> =
        this.sortedWith(compareByDescending { it.creationDate })
}