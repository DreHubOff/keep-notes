package com.jksol.keep.notes.ui.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ActionBarDefaults {

    val contentHeight = 46.dp
    val horizontalPadding = 16.dp
    val extraPaddingTop = 20.dp

    @Composable
    fun contentColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant
}