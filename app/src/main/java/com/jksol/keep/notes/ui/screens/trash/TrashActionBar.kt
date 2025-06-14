@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.screens.trash

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.ThemedDropdownMenu
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans
import com.jksol.keep.notes.ui.theme.themedTopAppBarColors

@Composable
fun TrashActionBar(
    systemBarInset: Dp = 0.dp,
    showMenu: Boolean = false,
    onBackClick: () -> Unit = {},
    onEmptyTrashClick: () -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier,
        colors = themedTopAppBarColors(),
        windowInsets = WindowInsets(top = systemBarInset),
        title = {
            Text(
                text = stringResource(R.string.trash),
                fontWeight = FontWeight.SemiBold,
                fontFamily = plusJakartaSans,
                fontSize = 20.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                )
            }
        },
        actions = {
            if (showMenu) {
                ThemedDropdownMenu(
                    actions = listOf(
                        ThemedDropdownMenu.Action(
                            text = stringResource(id = R.string.action_empty_trash),
                            onClick = onEmptyTrashClick
                        ),
                    )
                )
            }
        }
    )
}

@Preview
@Composable
private fun PreviewNoMenu() {
    ApplicationTheme {
        TrashActionBar(
            systemBarInset = 10.dp,
        )
    }
}

@Preview
@Composable
private fun PreviewMenu() {
    ApplicationTheme {
        TrashActionBar(
            systemBarInset = 10.dp,
            showMenu = true,
        )
    }
}