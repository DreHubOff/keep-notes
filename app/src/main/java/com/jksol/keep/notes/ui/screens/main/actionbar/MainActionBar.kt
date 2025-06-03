@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.main.actionbar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.main.model.MainScreenState
import com.jksol.keep.notes.ui.screens.main.search.SearchBarDefaults
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedTopAppBarColors

@Composable
fun MainActionBar(
    modifier: Modifier = Modifier,
    state: MainScreenState,
    scrollBehavior: TopAppBarScrollBehavior,
    onEvent: (MainActionBarIntent) -> Unit = {},
) {
    val navigationBarHeight = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val expandedHeight: Dp = remember(navigationBarHeight) {
        navigationBarHeight +
                SearchBarDefaults.searchButtonHeight +
                SearchBarDefaults.searchButtonExtraPaddingTop
    }
    val windowInsets = WindowInsets.systemBars

    when {
        state.isSelectionMode -> SelectionModeToolbar(
            modifier = modifier,
        )

        state.searchEnabled -> SearchModeToolbar(
            modifier = modifier,
            searchPrompt = state.searchPrompt.orEmpty(),
            expandedHeight = expandedHeight,
            windowInsets = windowInsets,
            onEvent = onEvent,
        )

        else -> RegularModeToolbar(
            modifier = modifier,
            expandedHeight = expandedHeight,
            windowInsets = windowInsets,
            scrollBehavior = scrollBehavior,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun RegularModeToolbar(
    modifier: Modifier,
    expandedHeight: Dp,
    windowInsets: WindowInsets,
    scrollBehavior: TopAppBarScrollBehavior,
    onEvent: (MainActionBarIntent) -> Unit,
) {
    TopAppBar(
        modifier = modifier
            .padding(end = SearchBarDefaults.searchButtonHorizontalPadding),
        scrollBehavior = scrollBehavior,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = themedTopAppBarColors(),
        title = {
            Box(
                modifier = Modifier
                    .height(SearchBarDefaults.searchButtonHeight)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SearchBarDefaults.searchButtonCornerRadius))
                    .background(SearchBarDefaults.searchBackgroundColor())
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onEvent(MainActionBarIntent.OpenSideMenu) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.Menu,
                            contentDescription = stringResource(R.string.menu_desc),
                            tint = SearchBarDefaults.searchContentColor()
                        )
                    }
                    TextButton(
                        onClick = { onEvent(MainActionBarIntent.OpenSearch) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 0.dp)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.search_notes),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun SearchModeToolbar(
    modifier: Modifier,
    searchPrompt: String,
    expandedHeight: Dp,
    windowInsets: WindowInsets,
    onEvent: (MainActionBarIntent) -> Unit,
) {
    var searchPromptLocal by remember(searchPrompt) { mutableStateOf(searchPrompt ?: "") }
    TopAppBar(
        modifier = modifier,
        scrollBehavior = null,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = themedTopAppBarColors(),
        navigationIcon = {
            IconButton(onClick = { onEvent(MainActionBarIntent.HideSearch) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = stringResource(R.string.close_search_bar_desc),
                )
            }
        },
        actions = {
            if (searchPromptLocal.isNotEmpty()) {
                IconButton(onClick = {
                    searchPromptLocal = ""
                    onEvent(MainActionBarIntent.OpenSearchPromptChanged(""))
                }) {
                    Icon(
                        imageVector = Icons.Sharp.Close,
                        contentDescription = stringResource(R.string.clear_search_bar_desc)
                    )
                }
            }
        },
        title = {
            TextField(
                value = searchPromptLocal,
                onValueChange = { newValue ->
                    searchPromptLocal = newValue
                    onEvent(MainActionBarIntent.OpenSearchPromptChanged(newValue))
                },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_notes),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                textStyle = TextStyle(),
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
    )
}

@Composable
private fun SelectionModeToolbar(modifier: Modifier) {

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun Idle() {
    ApplicationTheme {
        Scaffold(
            topBar = {
                MainActionBar(
                    state = MainScreenState.EMPTY,
                    scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                )
            },
        ) { _ ->
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun Search() {
    ApplicationTheme {
        Scaffold(
            topBar = {
                MainActionBar(
                    state = MainScreenState.EMPTY.copy(
                        searchEnabled = true,
                        searchPrompt = "Search for..."
                    ),
                    scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                )
            },
        ) { _ ->
        }
    }
}
