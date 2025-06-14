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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.themedCheckboxColors

@Composable
fun ChecklistCheckbox(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val translationX = -40.dp.value
        Checkbox(
            modifier = Modifier
                .scale(0.8f)
                .height(28.dp)
                .graphicsLayer { this.translationX = translationX }
                .clickable(enabled = true, onClick = onClick),
            checked = checked,
            onCheckedChange = { onClick() },
            enabled = enabled,
            colors = themedCheckboxColors(),
        )
        Text(
            modifier = Modifier
                .graphicsLayer { this.translationX = translationX },
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        )
    }
}

@Preview(name = "Checked", showBackground = true)
@Composable
private fun PreviewSelectedNull() {
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