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
                sequenceOf(textNotes, checklists)
                    .flatten()
                    .let { sequence ->
                        if (searchPrompt.trim().isEmpty()) {
                            sequence
                        } else {
                            sequence.filter { item ->
                                searchInTitle(prompt = searchPrompt, title = item.title) || searchInContent(searchPrompt, item)
                            }
                        }
                    }
                    .sortedByPinnedAndModificationDate()
                    .toList()
            }
    }

    private fun searchInContent(prompt: String, type: ApplicationMainDataType): Boolean {
        return when (type) {
            is Checklist -> type.items.any { item -> searchInTitle(prompt = prompt, title = item.title) }
            is TextNote -> searchInTitle(prompt = prompt, title = type.content)
        }
    }

    private fun searchInTitle(prompt: String, title: String): Boolean {
        val titleWords = title.split(" ").filter { it.isNotEmpty() }
        val promptWords = prompt.split(" ").filter { it.isNotEmpty() }
        if (titleWords.isEmpty() || promptWords.size > titleWords.size) return false
        for (word in promptWords) {
            if (!titleWords.any { it.contains(word, ignoreCase = true) }) {
                return false
            }
        }
        return true
    }

    private fun <T : SortableListItem> Sequence<T>.sortedByPinnedAndModificationDate(): Sequence<T> {
        return this.sortedWith(
            compareByDescending<T> { it.isPinned }.thenByDescending { it.creationDate }
        )
    }
}