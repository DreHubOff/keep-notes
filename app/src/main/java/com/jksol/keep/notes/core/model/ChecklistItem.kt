package com.jksol.keep.notes.core.model

data class ChecklistItem(
    val id: Long,
    val title: String,
    val isChecked: Boolean,
    val listPosition: Int,
)
