package com.jksol.keep.notes

import com.jksol.keep.notes.core.model.ChecklistItem

object EditChecklistDemoData {

    val uncheckedChecklistItems = listOf(
        ChecklistItem(
            id = 1,
            title = "🛒 Buy groceries for the week",
            isChecked = false,
            listPosition = 0
        ),
        ChecklistItem(
            id = 2,
            title = "📞 Call Mom and check in",
            isChecked = false,
            listPosition = 1
        ),
        ChecklistItem(
            id = 3,
            title = "📚 Read 20 pages of a book",
            isChecked = false,
            listPosition = 2
        ),
        ChecklistItem(
            id = 4,
            title = "🏃 Go for a 30-minute jog",
            isChecked = false,
            listPosition = 3
        ),
        ChecklistItem(
            id = 5,
            title = "💻 Finish coding the checklist feature",
            isChecked = false,
            listPosition = 4
        )
    )

    val checkedChecklistItems = uncheckedChecklistItems.map {
        it.copy(isChecked = true)
    }
}