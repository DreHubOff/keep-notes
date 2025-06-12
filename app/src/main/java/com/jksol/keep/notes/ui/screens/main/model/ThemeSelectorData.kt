package com.jksol.keep.notes.ui.screens.main.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.jksol.keep.notes.core.model.ThemeType

@Stable
data class ThemeSelectorData(
    val options: List<ThemeOption>,
)

data class ThemeOption(
    @StringRes val nameRes: Int,
    val type: ThemeType,
    val isSelected: Boolean,
)
