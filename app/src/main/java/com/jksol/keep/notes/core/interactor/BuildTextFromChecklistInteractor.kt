package com.jksol.keep.notes.core.interactor

import com.jksol.keep.notes.core.model.MainTypeTextRepresentation
import com.jksol.keep.notes.data.ChecklistRepository
import javax.inject.Inject

class BuildTextFromChecklistInteractor @Inject constructor(
    private val checklistRepository: ChecklistRepository,
) {

    suspend operator fun invoke(checklistId: Long): MainTypeTextRepresentation? {
        val checklist = checklistRepository.getChecklistById(checklistId) ?: return null
        val content = buildString {
            val checkedItems = checklist
                .items
                .sortedBy { it.listPosition }
                .mapNotNull { item ->
                    if (item.isChecked) {
                        item
                    } else {
                        append("[ ] ")
                        appendLine(item.title)
                        null
                    }
                }
            checkedItems.forEach { item ->
                append("[X] ")
                appendLine(item.title)
            }
        }
        return MainTypeTextRepresentation(
            title = checklist.title,
            content = content,
        )
    }
}