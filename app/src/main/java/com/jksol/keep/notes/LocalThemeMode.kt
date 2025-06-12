package com.jksol.keep.notes

import androidx.compose.runtime.compositionLocalOf

enum class ThemeMode {
    DARK, LIGHT,
}

val LocalThemeMode = compositionLocalOf<ThemeMode> { ThemeMode.LIGHT }