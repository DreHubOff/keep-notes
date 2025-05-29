package com.jksol.keep.notes.demo_data

import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.shared.listitem.TextNoteCardData
import java.util.concurrent.atomic.AtomicLong

object MainScreenDemoData {

    private val idsGen = AtomicLong(0)

    object TextNotes {
        fun MainScreenItem.TextNote.asCardData(): TextNoteCardData {
            return TextNoteCardData(
                transitionKey = Unit,
                title = title,
                content = content,
            )
        }

        val welcomeBanner
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "Welcome to Your Notes! ✨",
                content = "This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                interactive = false,
            )

        val reminderPinnedNote
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "(R + PINNED) 🎉 Birthday Reminder",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                hasScheduledReminder = true,
                isPinned = true,
            )

        val reminderOnlyNote
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "(R) 🎉 Birthday Reminder",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                hasScheduledReminder = true,
            )

        val pinnedOnlyNote
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "(PINNED) 🎉 Birthday Reminder",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                isPinned = true,
            )

        val reminderPinnedNoteEmptyTitle
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "",
                content = "(R + PINNED) This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                hasScheduledReminder = true,
                isPinned = true,
            )

        val reminderPinnedNoteLongTitle
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "(R + PINNED) This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                content = "This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget.",
                hasScheduledReminder = true,
                isPinned = true,
            )

        val emptyTitleNote
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "",
                content = "Don’t forget Anna’s birthday on June 5th! 🎂 Don’t forget Anna’s birthday on June 5th! \uD83C\uDF82",
                hasScheduledReminder = true,
                isPinned = true,
            )
        val emptyContentNote
            get() = MainScreenItem.TextNote(
                id = idsGen.getAndIncrement(),
                title = "(R) \uD83C\uDF89 Birthday Reminder",
                content = "",
                hasScheduledReminder = true,
                isPinned = true,
            )
    }

    object CheckLists {
        val reminderPinnedChecklist
            get() = MainScreenItem.Checklist(
                id = idsGen.getAndIncrement(),
                title = "(R + PINNED) 🛒 Grocery Run",
                items = listOf(
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Apples 🍎"),
                    MainScreenItem.Checklist.Item(isChecked = true, text = "Chicken 🐔"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Spinach 🥬")
                ),
                tickedItems = 1,
                hasScheduledReminder = true,
                isPinned = true
            )

        val reminderOnlyChecklist
            get() = MainScreenItem.Checklist(
                id = idsGen.getAndIncrement(),
                title = "(R) 📅 Morning Routine",
                items = listOf(
                    MainScreenItem.Checklist.Item(isChecked = true, text = "Make coffee ☕"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Stretch 🤸‍♀️"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Check emails 📧")
                ),
                tickedItems = 1,
                hasScheduledReminder = true
            )

        val pinnedOnlyChecklist
            get() = MainScreenItem.Checklist(
                id = idsGen.getAndIncrement(),
                title = "(PINNED) 📚 Reading List",
                items = listOf(
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Clean Code 📕"),
                    MainScreenItem.Checklist.Item(isChecked = true, text = "Effective Java 📒"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Kotlin in Action 📗")
                ),
                tickedItems = 1,
                isPinned = true
            )

        val emptyTitleChecklist
            get() = MainScreenItem.Checklist(
                id = idsGen.getAndIncrement(),
                title = "",
                items = listOf(
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Task A"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Task B"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Task C")
                )
            )

        val emptyContentChecklist
            get() = MainScreenItem.Checklist(
                id = idsGen.getAndIncrement(),
                title = "(R + PINNED) This is a checklist",
                items = listOf()
            )

        val longTitleChecklist
            get() = MainScreenItem.Checklist(
                id = idsGen.getAndIncrement(),
                title = "(R + PINNED) This is a very long checklist title to test wrapping and overflow behavior in previews 📋✨",
                items = listOf(
                    MainScreenItem.Checklist.Item(isChecked = true, text = "Step 1 ✔️"),
                    MainScreenItem.Checklist.Item(isChecked = false, text = "Step 2 ➡️"),
                    MainScreenItem.Checklist.Item(isChecked = true, text = "Step 3 ✔️")
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