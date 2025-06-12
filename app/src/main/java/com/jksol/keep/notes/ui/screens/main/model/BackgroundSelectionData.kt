package com.jksol.keep.notes.ui.screens.main.model

import androidx.compose.ui.graphics.Color
import com.jksol.keep.notes.core.model.NoteColor

data class BackgroundSelectionData(
    val colors: List<NoteColor?>,
    val selectedColor: NoteColor?,
)