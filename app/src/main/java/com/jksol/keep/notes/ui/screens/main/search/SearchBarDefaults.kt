package com.jksol.keep.notes.ui.screens.main.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.ui.shared.ActionBarDefaults

object SearchBarDefaults {

    val searchButtonHeight = ActionBarDefaults.contentHeight
    val searchButtonHorizontalPadding = ActionBarDefaults.horizontalPadding
    val searchButtonCornerRadius = 26.dp
    val searchButtonExtraPaddingTop = ActionBarDefaults.extraPaddingTop

    @Composable
    fun searchBackgroundColor(): Color = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    fun searchContentColor(): Color = ActionBarDefaults.contentColor()
}