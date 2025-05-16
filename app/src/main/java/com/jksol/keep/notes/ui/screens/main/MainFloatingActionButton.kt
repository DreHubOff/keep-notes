package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun MainFloatingActionButton(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(bottom = 20.dp)
    ) {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.background,
            onClick = { onClick() },
        ) {
            Icon(Icons.Sharp.Add, stringResource(R.string.main_floating_button_desc))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainFloatingActionButton()
    }
}