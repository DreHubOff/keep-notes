package com.jksol.keep.notes.ui.screens.main.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object SearchBarDefaults {

    val searchButtonHeight = 46.dp
    val searchButtonHorizontalPadding = 16.dp
    val searchButtonCornerRadius = 26.dp
    val searchButtonExtraPaddingTop = 20.dp

    @Composable
    fun searchBackgroundColor(): Color = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    fun searchContentColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant
}