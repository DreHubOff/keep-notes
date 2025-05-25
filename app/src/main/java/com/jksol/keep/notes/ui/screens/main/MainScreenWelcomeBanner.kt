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
import com.jksol.keep.notes.demo_data.MainScreenDemoData
import com.jksol.keep.notes.ui.screens.trash.listitem.TrashTextNote
import com.jksol.keep.notes.ui.screens.main.model.MainScreenItem
import com.jksol.keep.notes.ui.screens.main.search.MainSearchBarEntryPoint
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainScreenWelcomeBanner(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    banner: MainScreenItem.TextNote,
    onToggleSearchVisibility: () -> Unit = {},
    onOpenMenuClick: () -> Unit = {},
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = spacedBy(8.dp),
    ) {
        MainSearchBarEntryPoint(
            innerPadding = innerPadding,
            modifier = Modifier.padding(bottom = 8.dp),
            onSearchClick = onToggleSearchVisibility,
            onOpenMenuClick = onOpenMenuClick,
        )

        TrashTextNote(
            modifier = Modifier.padding(horizontal = 8.dp),
            item = banner,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainScreenWelcomeBanner(
            innerPadding = PaddingValues(20.dp),
            banner = MainScreenDemoData.TextNotes.welcomeBanner,
        )
    }
}