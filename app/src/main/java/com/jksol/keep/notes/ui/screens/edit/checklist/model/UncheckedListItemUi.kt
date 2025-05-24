package com.jksol.keep.notes.ui.screens.edit.checklist.model

import com.jksol.keep.notes.ui.focus.ElementFocusRequest

data class UncheckedListItemUi(
    val id: Long,
    val text: String,
    val focusRequest: ElementFocusRequest?,
)