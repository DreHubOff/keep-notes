package com.jksol.keep.notes.ui.screens.main.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay

@Composable
fun MainSearchBar(
    innerPadding: PaddingValues,
    onHideSearch: () -> Unit = {},
    onValueChanged: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSearch by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        showSearch = true
    }

    val collapsedHeight = SearchBarDefaults.searchButtonHeight
    val collapsedPadding = SearchBarDefaults.searchButtonHorizontalPadding
    val collapsedRadius = SearchBarDefaults.searchButtonCornerRadius

    val expandedHeight =
        innerPadding.calculateTopPadding() +
                SearchBarDefaults.searchButtonHeight +
                SearchBarDefaults.searchButtonExtraPaddingTop

    val transition = updateTransition(showSearch, label = "SearchBarTransition")

    val height by transition.animateDp(label = "Height") { expanded ->
        if (expanded) expandedHeight else collapsedHeight
    }
    val horizontalPad by transition.animateDp(label = "HorizontalPad") { expanded ->
        if (expanded) 0.dp else collapsedPadding
    }
    val topPad by transition.animateDp(label = "TopPad") { expanded ->
        if (expanded) 0.dp else (expandedHeight - collapsedHeight)
    }
    val radius by transition.animateDp(label = "Radius") { expanded ->
        if (expanded) 0.dp else collapsedRadius
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(expandedHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = topPad, start = horizontalPad, end = horizontalPad)
                .height(height)
                .background(
                    color = SearchBarDefaults.searchBackgroundColor(),
                    shape = RoundedCornerShape(radius)
                )
        )
        AnimatedVisibility(visible = showSearch) {
            var searchPrompt by remember { mutableStateOf("") }
            SearchBarContent(
                value = searchPrompt,
                modifier = Modifier
                    .padding(horizontalPad)
                    .focusRequester(focusRequester),
                onValueChange = {
                    onValueChanged(it)
                    searchPrompt = it
                },
                onClearClick = {
                    onValueChanged("")
                    searchPrompt = ""
                },
                onCancelClick = {
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                    focusManager.clearFocus()
                    showSearch = false
                },
                onDoneClick = {
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                    focusManager.clearFocus()
                }
            )
        }
    }

    LaunchedEffect(showSearch) {
        delay(330)
        if (showSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            onHideSearch()
        }
    }
}

@Composable
private fun SearchBarContent(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(stringResource(R.string.search_notes), color = MaterialTheme.colorScheme.onSurface)
        },
        leadingIcon = {
            IconButton(onClick = onCancelClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = stringResource(R.string.close_search_bar_desc),
                )
            }
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Sharp.Close,
                        contentDescription = stringResource(R.string.clear_search_bar_desc)
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDoneClick() }),
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
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainSearchBar(
            innerPadding = PaddingValues(10.dp),
        )
    }
}