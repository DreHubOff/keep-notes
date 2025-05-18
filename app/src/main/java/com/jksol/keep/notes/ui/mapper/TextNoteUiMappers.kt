package com.jksol.keep.notes.ui.mapper

import com.jksol.keep.notes.core.model.TextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem

fun TextNote.toMainScreenItem(): MainScreenItem.TextNote {
    return MainScreenItem.TextNote(
        id = this.id,
        title = this.title,
        content = this.content,
        isPinned = this.isPinned,
        hasScheduledReminder = this.hasReminder,
        interactive = true
    )
}