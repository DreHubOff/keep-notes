package com.jksol.keep.notes.ui.screens.edit.checklist

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.shared.ChecklistCheckbox
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun DraggableChecklistItem(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Sharp.DragIndicator,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.drag_current_item)
        )
        ChecklistCheckbox(
            modifier = Modifier,
            text = title,
            checked = checked,
            enabled = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ApplicationTheme {
        DraggableChecklistItem(
            title = "This is a title",
            checked = true,
        )
    }
}