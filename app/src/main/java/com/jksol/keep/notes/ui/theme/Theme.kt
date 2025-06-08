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

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = VividBlue,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    primaryContainer = VividBlue,
    secondaryContainer = LightSkyBlue,
    surfaceContainerLow = SoftGlacier,
    surfaceContainerHigh = SoftGlacier,
    onSurface = SemiGray,
    surfaceVariant = IceBlue,
    onSurfaceVariant = Color.Black,
    error = BloodRed,
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
fun themedCardColors(isSelected: Boolean): CardColors {
    return CardDefaults.outlinedCardColors().copy(
        containerColor = if (isSelected) LightBlueOverlay else MaterialTheme.colorScheme.background,
    )
}

@Composable
fun themedCardBorder(isSelected: Boolean): BorderStroke {
    val defaults = CardDefaults.outlinedCardBorder()
    return BorderStroke(
        width = if (isSelected) defaults.width * 2 else defaults.width,
        color = if (isSelected) VividBlue else MorningFog
    )
}

@Composable
fun themedTimePickerColors(): TimePickerColors {
    return TimePickerDefaults.colors().copy(
        clockDialColor = Color(0xFFE0E7FF),
        selectorColor = VividBlue,
        periodSelectorSelectedContainerColor = Color(0xFFE0E7FF),
        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        periodSelectorBorderColor = Color.LightGray,
        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        timeSelectorUnselectedContainerColor = Color(0xFFEDF1FF),
        timeSelectorSelectedContentColor = VividBlue,
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