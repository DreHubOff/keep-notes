package com.jksol.keep.notes.core.interactor

import com.jksol.keep.notes.core.model.NoteColor
import javax.inject.Inject

class BuildNoteBackgroundColorListInteractor @Inject constructor() {

    operator fun invoke(): List<Long> {

        // TODO: add theme check here
        val isLightTheme = true
        return NoteColor.entries.map { noteColor ->
            if (isLightTheme) {
                noteColor.day
            } else {
                noteColor.night
            }
        }
    }
}