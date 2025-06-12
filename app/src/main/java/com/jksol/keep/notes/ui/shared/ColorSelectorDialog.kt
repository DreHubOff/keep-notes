@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatColorReset
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.LocalThemeMode
import com.jksol.keep.notes.ThemeMode
import com.jksol.keep.notes.core.model.NoteColor
import com.jksol.keep.notes.ui.theme.ApplicationTheme

@Composable
fun ColorSelectorDialog(
    title: String,
    colors: List<NoteColor?>,
    selectedColor: NoteColor?,
    onDismiss: () -> Unit,
    onColorSelected: (NoteColor?) -> Unit,
    selectedColorUndefined: Boolean = false,
) {
    BasicAlertDialog(
        modifier = Modifier,
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                )
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(40.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement
                        .spacedBy(16.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth(),
                    userScrollEnabled = false,
                    overscrollEffect = null,
                ) {
                    items(colors) { color ->
                        val cellColor = if (LocalThemeMode.current == ThemeMode.DARK) {
                            color?.night
                        } else {
                            color?.day
                        }?.let(::Color) ?: Color.Transparent
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = cellColor, shape = CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(color) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == null && selectedColor != null) {
                                Icon(
                                    imageVector = Icons.Outlined.FormatColorReset,
                                    contentDescription = "No Color",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (!selectedColorUndefined && selectedColor == color) {
                                Icon(
                                    imageVector = Icons.Sharp.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSelectedNull() {
    ApplicationTheme {
        ColorSelectorDialog(
            title = "Note Color",
            colors = mutableListOf(null) + NoteColor.entries,
            selectedColor = null,
            onDismiss = {},
            onColorSelected = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSelectedNonNull() {
    ApplicationTheme {
        ColorSelectorDialog(
            title = "Note Color",
            colors = mutableListOf(null) + NoteColor.entries,
            selectedColor = NoteColor.Lilac,
            onDismiss = {},
            onColorSelected = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSelectedUndefined() {
    ApplicationTheme {
        ColorSelectorDialog(
            title = "Note Color",
            colors = mutableListOf(null) + NoteColor.entries,
            selectedColor = NoteColor.Lime,
            selectedColorUndefined = true,
            onDismiss = {},
            onColorSelected = {},
        )
    }
}
