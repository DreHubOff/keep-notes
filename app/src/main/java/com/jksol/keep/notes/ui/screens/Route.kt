package com.jksol.keep.notes.ui.screens

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object MainScreen : Route()

    @Serializable
    data class EditNoteScreen(val noteId: Long?) : Route() {

        sealed class Result : Parcelable {

            @Parcelize
            data class Edited(val noteId: Long) : Result()

            @Parcelize
            data class Trashed(val noteId: Long) : Result()

            companion object {
                val KEY: String = Result::class.java.name
            }
        }
    }

    @Serializable
    data class EditChecklistScreen(val checklistId: Long?) : Route() {

        sealed class Result : Parcelable {

            @Parcelize
            data class Edited(val checklistId: Long) : Result()

            @Parcelize
            data class Trashed(val checklistId: Long) : Result()

            companion object {
                val KEY: String = Result::class.java.name
            }
        }
    }
}
