package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jksol.keep.notes.MainScreenDemoData.noNotes
import com.jksol.keep.notes.MainScreenDemoData.notesList
import com.jksol.keep.notes.MainScreenDemoData.welcomeBanner
import com.jksol.keep.notes.ui.screens.main.fab.FabContainer
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val items = viewModel.listItems
    ScreenContent(items)
}

@Composable
private fun ScreenContent(listItems: List<MainScreenItem> = emptyList()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        if (listItems.isEmpty()) {
            MainScreenEmptyList()
        } else {
            Box {
                MainScreenList(innerPadding, listItems)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(innerPadding.calculateTopPadding() + 4.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                )
            }
        }

        FabContainer(
            onAddTextNoteClick = { },
            onAddChecklistClick = { },
        )
    }
}

@Composable
private fun MainScreenList(innerPadding: PaddingValues, listItems: List<MainScreenItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp, bottom = 120.dp, start = 8.dp, end = 8.dp
        ),
        verticalArrangement = spacedBy(8.dp),
    ) {
        item {
            MainSearchBar(innerPadding)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(listItems) { item ->
            when (item) {
                is MainScreenItem.CheckList -> MainCheckList(item)
                is MainScreenItem.TextNote -> MainTextNote(item)
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=double",
    showSystemUi = true,
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1080px,height=2340px,dpi=440,cutout=double",
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun MainScreenPreview(@PreviewParameter(PreviewBinder::class) stateList: List<MainScreenItem>) {
    ApplicationTheme {
        ScreenContent(stateList)
    }
}

private class PreviewBinder : PreviewParameterProvider<List<MainScreenItem>> {
    override val values: Sequence<List<MainScreenItem>>
        get() = sequenceOf(
            noNotes(),
            welcomeBanner(),
            notesList(),
        )
}