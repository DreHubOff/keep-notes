package com.jksol.keep.notes.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jksol.keep.notes.R
import com.jksol.keep.notes.core.model.ThemeType
import com.jksol.keep.notes.ui.screens.main.model.ThemeOption
import com.jksol.keep.notes.ui.shared.AppAlertDialog
import com.jksol.keep.notes.ui.theme.ApplicationTheme
import com.jksol.keep.notes.ui.theme.plusJakartaSans

@Composable
fun ThemeSelectorDialog(
    title: String,
    themeOptions: List<ThemeOption>,
    onDismiss: () -> Unit = {},
    onThemeSelected: (ThemeType) -> Unit = {},
) {
    AppAlertDialog(
        dismissAction = onDismiss,
        confirmAction = onDismiss,
        confirmButtonText = stringResource(R.string.cancel),
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = plusJakartaSans,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                themeOptions.forEach { option ->
                    key(option) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(shape = MaterialTheme.shapes.extraLarge)
                                .selectable(
                                    selected = option.isSelected,
                                    onClick = { onThemeSelected(option.type) },
                                    role = Role.RadioButton,
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = option.isSelected, onClick = null)
                            Text(
                                text = stringResource(option.nameRes),
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp,
                                    fontFamily = plusJakartaSans,
                                    fontSize = 16.sp,
                                ),
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ThemeSelectorDialog(
            title = stringResource(R.string.choose_theme),
            themeOptions = listOf(
                ThemeOption(
                    nameRes = R.string.light,
                    type = ThemeType.LIGHT,
                    isSelected = true,
                ),
                ThemeOption(
                    nameRes = R.string.dark,
                    type = ThemeType.DARK,
                    isSelected = false,
                ),
                ThemeOption(
                    nameRes = R.string.system_default,
                    type = ThemeType.SYSTEM_DEFAULT,
                    isSelected = false,
                )
            )
        )
    }
}