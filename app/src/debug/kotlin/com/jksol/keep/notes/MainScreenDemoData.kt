package com.jksol.keep.notes

import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem

object MainScreenDemoData {

    object TextNotes {
        val welcomeBanner
            get() = MainScreenItem.TextNote(
                title = "Welcome to Your Notes! ✨",
                content = "This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                interactive = false,
            )

        val reminderPinnedNote
            get() = MainScreenItem.TextNote(
                title = "(R + PINNED) 🎉 Birthday Reminder",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                hasScheduledReminder = true,
                isPinned = true,
            )

        val reminderOnlyNote
            get() = MainScreenItem.TextNote(
                title = "(R) 🎉 Birthday Reminder",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                hasScheduledReminder = true,
            )

        val pinnedOnlyNote
            get() = MainScreenItem.TextNote(
                title = "(PINNED) 🎉 Birthday Reminder",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                isPinned = true,
            )

        val reminderPinnedNoteEmptyTitle
            get() = MainScreenItem.TextNote(
                title = "",
                content = "(R + PINNED) This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                hasScheduledReminder = true,
                isPinned = true,
            )

        val reminderPinnedNoteLongTitle
            get() = MainScreenItem.TextNote(
                title = "(R + PINNED) This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                content = "This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                hasScheduledReminder = true,
                isPinned = true,
            )

        val emptyTitleNote
            get() = MainScreenItem.TextNote(
                title = "",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                hasScheduledReminder = true,
                isPinned = true,
            )
    }

    object CheckLists {
        // 1. (R + PINNED) checklist
        val reminderPinnedChecklist
            get() = MainScreenItem.CheckList(
                title = "(R + PINNED) 🛒 Grocery Run",
                items = listOf(
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Apples 🍎"),
                    MainScreenItem.CheckList.Item(isChecked = true, text = "Chicken 🐔"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Spinach 🥬")
                ),
                tickedItems = 1,
                hasScheduledReminder = true,
                isPinned = true
            )

        // 2. (R) only
        val reminderOnlyChecklist
            get() = MainScreenItem.CheckList(
                title = "(R) 📅 Morning Routine",
                items = listOf(
                    MainScreenItem.CheckList.Item(isChecked = true, text = "Make coffee ☕"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Stretch 🤸‍♀️"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Check emails 📧")
                ),
                tickedItems = 1,
                hasScheduledReminder = true
            )

        // 3. (PINNED) only
        val pinnedOnlyChecklist
            get() = MainScreenItem.CheckList(
                title = "(PINNED) 📚 Reading List",
                items = listOf(
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Clean Code 📕"),
                    MainScreenItem.CheckList.Item(isChecked = true, text = "Effective Java 📒"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Kotlin in Action 📗")
                ),
                tickedItems = 1,
                isPinned = true
            )

        // 4. Empty title
        val emptyTitleChecklist
            get() = MainScreenItem.CheckList(
                title = "",
                items = listOf(
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Task A"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Task B"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Task C")
                )
            )

        // 5. Long title
        val longTitleChecklist
            get() = MainScreenItem.CheckList(
                title = "(R + PINNED) This is a very long checklist title to test wrapping and overflow behavior in previews 📋✨",
                items = listOf(
                    MainScreenItem.CheckList.Item(isChecked = true, text = "Step 1 ✔️"),
                    MainScreenItem.CheckList.Item(isChecked = false, text = "Step 2 ➡️"),
                    MainScreenItem.CheckList.Item(isChecked = true, text = "Step 3 ✔️")
                ),
                tickedItems = 2,
                hasScheduledReminder = true,
                isPinned = true
            )
    }

    fun noNotes() = emptyList<MainScreenItem>()

    fun welcomeBanner() = listOf(TextNotes.welcomeBanner)

    fun notesList() = listOf(
        TextNotes.emptyTitleNote,
        CheckLists.longTitleChecklist,
        TextNotes.reminderPinnedNote,
        TextNotes.reminderOnlyNote,
        CheckLists.reminderPinnedChecklist,
        TextNotes.pinnedOnlyNote,
        TextNotes.reminderPinnedNoteEmptyTitle,
        CheckLists.emptyTitleChecklist,
        TextNotes.reminderPinnedNoteLongTitle,
    )
}