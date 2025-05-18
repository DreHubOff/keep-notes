package com.jksol.keep.notes.ui.screens

sealed class Route {

    val route: String = this::class.java.name

    class MainScreen : Route()
    class EditNoteScreen : Route()
}
