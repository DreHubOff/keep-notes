package com.jksol.keep.notes.ui.shared

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun ChecklistCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    enabled: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
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
            onCheckedChange = { onCheckedChange(it) },
            enabled = enabled,
        )
        Text(
            modifier = Modifier
                .clickable(enabled = enabled) { onCheckedChange(!checked) }
                .graphicsLayer { this.translationX = translationX },
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(name = "Checked", showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        ChecklistCheckbox(
            text = "This is a text",
            checked = true,
        )
    }
}

@Preview(name = "Not checked", showBackground = true)
@Composable
private fun PreviewNotChecked() {
    ApplicationTheme {
        ChecklistCheckbox(
            text = "This is a text",
            checked = false,
        )
    }
}