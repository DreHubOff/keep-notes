package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.ui.screens.main.search.MainSearchBarEntryPoint
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainScreenEmptyState(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onToggleSearchVisibility: () -> Unit = {},
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = spacedBy(8.dp),
    ) {
        MainSearchBarEntryPoint(
            innerPadding = innerPadding,
            modifier = Modifier.padding(bottom = 8.dp),
            onClick = onToggleSearchVisibility,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainScreenEmptyState(
            innerPadding = PaddingValues(20.dp),
        )
    }
}