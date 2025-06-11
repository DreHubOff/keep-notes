package com.jksol.keep.notes.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

fun Color.lighten(fraction: Float): Color = lerp(this, Color.White, fraction.coerceIn(0f, 1f))