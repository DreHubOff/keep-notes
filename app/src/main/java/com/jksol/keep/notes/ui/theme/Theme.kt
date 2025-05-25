@file:OptIn(ExperimentalMaterial3Api::class)

package com.jksol.keep.notes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
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