package com.jksol.keep.notes.ui.screens.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
        floatingActionButton = { MainFloatingActionButton() },
        floatingActionButtonPosition = FabPosition.End,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MainSearchBar(innerPadding)
            if (listItems.isEmpty()) {
                MainScreenEmptyList()
            } else {
                MainScreenList(innerPadding, listItems)
            }
        }
    }
}

@Composable
private fun MainScreenList(innerPadding: PaddingValues, listItems: List<MainScreenItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = innerPadding.calculateBottomPadding(),
            start = 8.dp,
            end = 8.dp
        ),
        verticalArrangement = spacedBy(8.dp),
    ) {
        items(listItems) { item ->
            when (item) {
                is MainScreenItem.CheckList -> MainCheckList(item)
                is MainScreenItem.TextNote -> MainTextNote(item)
            }
        }
    }
}

@Preview(
    device = "spec:width=1080px,height=2340px,dpi=440,isRound=true,cutout=double", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun MainScreenPreview() {
    ApplicationTheme {
        ScreenContent(
            listOf(
                MainScreenItem.TextNote(
                    "Welcome to Your Notes! ✨",
                    "This is where you can quickly save notes after calls — whether it’s an address, a follow-up task, or something you don’t want to forget."
                ),
                MainScreenItem.CheckList(
                    title = "Grocery List",
                    content = "Things to› buy this weekend",
                    items = listOf(
                        MainScreenItem.CheckList.Item(isChecked = true, text = "Milk"),
                        MainScreenItem.CheckList.Item(isChecked = false, text = "Eggs"),
                        MainScreenItem.CheckList.Item(isChecked = true, text = "Bread"),
                        MainScreenItem.CheckList.Item(isChecked = true, text = "Bread 1"),
                        MainScreenItem.CheckList.Item(isChecked = true, text = "Bread 2"),
                    ),
                    tickedItems = 12
                )
            )
        )
    }
}