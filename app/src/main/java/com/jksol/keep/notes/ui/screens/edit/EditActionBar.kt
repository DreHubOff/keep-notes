package com.jksol.keep.notes.ui.screens.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.ActionBarDefaults
import com.jksol.keep.notes.ui.shared.PinCheckbox
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun EditActionBar(
    systemBarInset: Dp = 0.dp,
    pinned: Boolean = false,
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
) {
    val fullHeight = remember {
        systemBarInset +
                ActionBarDefaults.extraPaddingTop +
                ActionBarDefaults.contentHeight
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(fullHeight)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ActionBarDefaults.contentHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            var checked by remember(pinned) { mutableStateOf(pinned) }
            PinCheckbox(
                isChecked = checked,
                onCheckedChange = {
                    onPinCheckedChange(it)
                    checked = it
                },
                contentDescription = stringResource(R.string.pin_this_note)
            )
        }
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditActionBarStateProvider::class) pinned: Boolean) {
    ApplicationTheme {
        EditActionBar(
            systemBarInset = 10.dp,
            pinned = pinned,
        )
    }
}

private class EditActionBarStateProvider() : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}