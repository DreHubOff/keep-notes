package com.jksol.keep.notes.ui.screens

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object MainScreen : Route()

    @Serializable
    data class EditNoteScreen(
        val noteId: Long? = null,
        val noteTitle: String? = null,
        val noteContent: String? = null,
    ) : Route()
}
