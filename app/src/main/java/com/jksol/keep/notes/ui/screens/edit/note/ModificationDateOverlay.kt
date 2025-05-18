package com.jksol.keep.notes.ui.screens.edit.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun ModificationDateOverlay(
    navigationBarPadding: Dp,
    message: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                .padding(top = 4.dp, bottom = navigationBarPadding + 4.dp, start = 8.dp, end = 8.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ModificationDateOverlay(navigationBarPadding = 10.dp, message = "This is a preview")
    }
}
