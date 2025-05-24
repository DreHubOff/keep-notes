package com.jksol.keep.notes.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChecklistItem(
    val id: Long,
    val title: String,
    val isChecked: Boolean,
    val listPosition: Int,
) : Parcelable {

    companion object {
        fun generateEmpty() = ChecklistItem(id = 0, title = "", isChecked = false, listPosition = 0)
    }
}
