package com.jksol.keep.notes.core.interactor

import com.jksol.keep.notes.core.model.ApplicationMainDataType
import javax.inject.Inject

class BuildNoteBackgroundColorInteractor @Inject constructor() {

    operator fun invoke(item: ApplicationMainDataType): Long? {

        // TODO: add theme check here
        val isLightTheme = true
        return if (isLightTheme) item.backgroundColor?.day else item.backgroundColor?.night
    }
}