@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jksol.keep.notes.LocalThemeMode
import com.jksol.keep.notes.ThemeMode
import com.jksol.keep.notes.core.model.NoteColor

private val DarkColorScheme = darkColorScheme(
    primary = AzureFlash,
    onSurfaceVariant = Color.White,
    onSurface = LightGray,
    surfaceVariant = NightGray,
    primaryContainer = AzureFlash,
    secondaryContainer = DarkOcean,
    inverseSurface = Color(0xFFDDECFD),
    inverseOnSurface = Color.Black,
    inversePrimary = Color(0xFF2CA0FD),
    surfaceContainerLow = Color(0xFF303D4C),
    surfaceContainerHigh = Color(0xFF303D4C),
    error = Color(0xFFEE7070),
    background = Color(0xFF0F131C)
)

private val LightColorScheme = lightColorScheme(
    primary = VividBlue,
    primaryContainer = VividBlue,
    secondaryContainer = LightSkyBlue,
    surfaceContainerLow = SoftGlacier,
    surfaceContainerHigh = SoftGlacier,
    onSurface = SemiGray,
    surfaceVariant = IceBlue,
    onSurfaceVariant = Color.Black,
    error = BloodRed,
    inverseSurface = Color(0xFF3A4148),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF78BBFC),
    background = Color(0xFFF6F9FE),
)

@Composable
fun themedCheckboxColors(): CheckboxColors {
    return with(MaterialTheme.colorScheme) {
        CheckboxColors(
            checkedCheckmarkColor = background,
            uncheckedCheckmarkColor = Color.Transparent,
            checkedBoxColor = onSurface,
            uncheckedBoxColor = Color.Transparent,
            disabledCheckedBoxColor = onSurface,
            disabledUncheckedBoxColor = Color.Transparent,
            disabledIndeterminateBoxColor = Color.Transparent,
            checkedBorderColor = onSurface,
            uncheckedBorderColor = onSurface,
            disabledBorderColor = onSurface,
            disabledUncheckedBorderColor = onSurface,
            disabledIndeterminateBorderColor = onSurface,
        )
    }
}

@Composable
fun themedDropdownMenuItemColors(): MenuItemColors {
    return with(MaterialTheme.colorScheme) {
        MenuItemColors(
            textColor = onSurfaceVariant,
            leadingIconColor = onSurfaceVariant,
            trailingIconColor = onSurfaceVariant,
            disabledTextColor = onSurface,
            disabledLeadingIconColor = onSurface,
            disabledTrailingIconColor = onSurface,
        )
    }
}

@Composable
fun themedTopAppBarColors(): TopAppBarColors {
    return with(MaterialTheme.colorScheme) {
        TopAppBarColors(
            containerColor = background,
            scrolledContainerColor = background,
            navigationIconContentColor = onSurfaceVariant,
            titleContentColor = onSurfaceVariant,
            actionIconContentColor = onSurfaceVariant,
        )
    }
}

@Composable
fun themedCardColors(isSelected: Boolean, customBackground: NoteColor?): CardColors {
    val isDarkTheme = LocalThemeMode.current == ThemeMode.DARK
    val backgroundColor = when {
        isSelected -> if (isDarkTheme) HintAqua else LightBlueOverlay
        customBackground != null -> Color(if (isDarkTheme) customBackground.night else customBackground.day)
        else -> MaterialTheme.colorScheme.background
    }
    return CardDefaults.outlinedCardColors().copy(containerColor = backgroundColor)
}

@Composable
fun themedCardBorder(isSelected: Boolean): BorderStroke {
    val defaults = CardDefaults.outlinedCardBorder()
    val isDarkTheme = LocalThemeMode.current == ThemeMode.DARK
    val selectedBorder = if (isDarkTheme) AzureFlash else VividBlue
    val notSelectedBorder = if (isDarkTheme) MorningFogLight else MorningFog
    return BorderStroke(
        width = if (isSelected) defaults.width * 2 else defaults.width,
        color = if (isSelected) selectedBorder else notSelectedBorder
    )
}

@Composable
fun themedTimePickerColors(): TimePickerColors {
    val isDarkTheme = LocalThemeMode.current == ThemeMode.DARK
    return TimePickerDefaults.colors().copy(
        clockDialColor = if (isDarkTheme) Color(0xFF374E67) else Color(0xFFE0E7FF),
        selectorColor = if (isDarkTheme) AzureFlash else VividBlue,
        periodSelectorSelectedContainerColor = if (isDarkTheme) Color(0xFF374E67) else Color(0xFFE0E7FF),
        periodSelectorUnselectedContainerColor =
            if (isDarkTheme) Color(0xFF364353) else MaterialTheme.colorScheme.surfaceContainerLow,
        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        periodSelectorBorderColor = if (isDarkTheme) Color(0x1AFFFFFF) else Color.LightGray,
        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        timeSelectorUnselectedContainerColor = if (isDarkTheme) Color(0xFF364353) else Color(0xFFEDF1FF),
        timeSelectorSelectedContentColor = if (isDarkTheme) AzureFlash else VividBlue,
        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        clockDialSelectedContentColor = MaterialTheme.colorScheme.surface,
        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun themedDatePickerColors(): DatePickerColors {
    return DatePickerDefaults.colors()
}

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}