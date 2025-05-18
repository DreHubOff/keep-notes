package com.jksol.keep.notes.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ChecklistCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    enabled: Boolean = false,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val translationX = -40.dp.value
        Checkbox(
            modifier = Modifier
                .scale(0.7f)
                .height(28.dp)
                .graphicsLayer { this.translationX = translationX },
            checked = checked,
            onCheckedChange = {

            },
            enabled = enabled,
        )
        Text(
            modifier = Modifier.graphicsLayer { this.translationX = translationX },
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}