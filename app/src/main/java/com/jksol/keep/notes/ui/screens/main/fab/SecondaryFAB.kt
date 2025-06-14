package com.jksol.keep.notes.ui.screens.main.fab

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ModeEdit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans

@Composable
fun SecondaryFAB(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes text: Int,
    @StringRes description: Int,
    onClick: () -> Unit = {},
) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(text),
                fontFamily = plusJakartaSans,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(icon, contentDescription = stringResource(description))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        SecondaryFAB(icon = Icons.Sharp.ModeEdit, text = R.string.fab_add_note, description = 0)
    }
}