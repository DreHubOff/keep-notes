package com.jksol.keep.notes.ui.screens.main.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.screens.main.search.SearchBarDefaults
import com.jksol.keep.notes.ui.shared.PinCheckbox
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SelectionActionBar(
    modifier: Modifier,
    innerPadding: PaddingValues,
    selectedItemCount: Int,
    isPinned: Boolean,
    onExitSelectionMode: () -> Unit = {},
    onMoveToTrashClick: () -> Unit = {},
    onPinnedStateChanged: (Boolean) -> Unit = {},
    onSelectBackgroundClick: () -> Unit = {},
) {
    var showActionBar by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showActionBar = true
    }

    val collapsedHeight = SearchBarDefaults.searchButtonHeight
    val collapsedPadding = SearchBarDefaults.searchButtonHorizontalPadding
    val collapsedRadius = SearchBarDefaults.searchButtonCornerRadius

    val expandedHeight =
        innerPadding.calculateTopPadding() +
                SearchBarDefaults.searchButtonHeight +
                SearchBarDefaults.searchButtonExtraPaddingTop

    val transition = updateTransition(showActionBar, label = "SelectionActionBarTransition")

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
        modifier
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
        val selectionText = stringResource(R.string.selected_count_pattern, selectedItemCount)
        val coroutineScope = rememberCoroutineScope()
        AnimatedVisibility(visible = showActionBar) {
            ActionBarContent(
                value = selectionText,
                modifier = Modifier.padding(horizontalPad),
                isPinned = isPinned,
                onCancelClick = {
                    coroutineScope.launch {
                        showActionBar = false
                        delay(150)
                        onExitSelectionMode()
                    }
                },
                onMoveToTrashClick = onMoveToTrashClick,
                onPinnedStateChanged = onPinnedStateChanged,
                onSelectBackgroundClick = onSelectBackgroundClick,
            )
        }
    }
}

@Composable
private fun ActionBarContent(
    modifier: Modifier = Modifier,
    value: String,
    isPinned: Boolean = false,
    onCancelClick: () -> Unit = {},
    onMoveToTrashClick: () -> Unit = {},
    onPinnedStateChanged: (Boolean) -> Unit = {},
    onSelectBackgroundClick: () -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = {},
        enabled = false,
        leadingIcon = {
            IconButton(onClick = onCancelClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        trailingIcon = {
            Row {
                IconButton(onClick = onSelectBackgroundClick) {
                    Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
                }
                PinCheckbox(
                    modifier = Modifier,
                    isChecked = isPinned,
                    onCheckedChange = onPinnedStateChanged,
                    contentDescription = "",
                )
                IconButton(onClick = onMoveToTrashClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.action_delete),
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = LocalTextStyle.current + TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        colors = TextFieldDefaults.colors(
            disabledContainerColor = Color.Transparent,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ActionBarContent(
            modifier = Modifier.background(SearchBarDefaults.searchBackgroundColor()),
            value = "3 Selected",
            isPinned = true,
        )
    }
}