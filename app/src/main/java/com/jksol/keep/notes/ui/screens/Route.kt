package com.jksol.keep.notes.ui.screens

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object MainScreen : Route()

    @Serializable
    data class EditNoteScreen(val noteId: Long?) : Route() {

        @Parcelize
        class Result(val noteId: Long) : Parcelable {
            companion object {
                val KEY: String = Result::class.java.name
            }
        }
    }

    @Serializable
    data class EditChecklistScreen(val checklistId: Long?) : Route() {

        @Parcelize
        class Result(val checklistId: Long) : Parcelable {
            companion object {
                val KEY: String = Result::class.java.name
            }
        }
    }
}
