package com.jksol.keep.notes.core.interactor

import com.jksol.keep.notes.core.model.ApplicationMainDataType
import com.jksol.keep.notes.core.model.Checklist
import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.data.ChecklistRepository
import com.jksol.keep.notes.data.TextNotesRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class PermanentlyDeleteApplicationMainDataTypeInteractor @Inject constructor(
    private val textNotesRepository: TextNotesRepository,
    private val checklistRepository: ChecklistRepository,
) {

    suspend operator fun invoke(vararg item: ApplicationMainDataType) {
        val textNotes = mutableListOf<TextNote>()
        val checklists = mutableListOf<Checklist>()
        item.forEach {
            when (it) {
                is TextNote -> textNotes.add(it)
                is Checklist -> checklists.add(it)
            }
        }
        supervisorScope {
            launch { textNotesRepository.delete(textNotes) }
            launch { checklistRepository.delete(checklists) }
        }
    }
}