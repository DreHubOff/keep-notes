package com.jksol.keep.notes

import com.jksol.keep.notes.ui.screens.edit.checklist.model.CheckedListItemUi
import com.jksol.keep.notes.ui.screens.edit.checklist.model.UncheckedListItemUi

object EditChecklistDemoData {

    val uncheckedChecklistItems = listOf(
        UncheckedListItemUi(
            id = 1,
            text = "🛒 Buy groceries for the week",
            isFocused = false
        ),
        UncheckedListItemUi(
            id = 2,
            text = "📞 Call Mom and check in",
            isFocused = true,
        ),
        UncheckedListItemUi(
            id = 3,
            text = "📚 Read 20 pages of a book",
            isFocused = false,
        ),
        UncheckedListItemUi(
            id = 4,
            text = "🏃 Go for a 30-minute jog",
            isFocused = false,
        ),
        UncheckedListItemUi(
            id = 5,
            text = "💻 Finish coding the checklist feature",
            isFocused = false,
        )
    )

    val checkedChecklistItems = listOf(
        CheckedListItemUi(
            id = 1,
            text = "🛒 Buy groceries for the week",
        ),
        CheckedListItemUi(
            id = 2,
            text = "📞 Call Mom and check in",
        ),
        CheckedListItemUi(
            id = 3,
            text = "📚 Read 20 pages of a book",
        ),
        CheckedListItemUi(
            id = 4,
            text = "🏃 Go for a 30-minute jog",
        ),
        CheckedListItemUi(
            id = 5,
            text = "💻 Finish coding the checklist feature",
        )
    )
}