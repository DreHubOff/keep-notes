package com.jksol.keep.notes.ui.shared

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.jksol.keep.notes.LocalThemeMode
import com.jksol.keep.notes.ThemeMode

@Composable
fun SetupSystemNavigationBars() {
    val useDarkIcons = LocalThemeMode.current == ThemeMode.DARK
    val activity = LocalActivity.current ?: return
    SideEffect {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        window.navigationBarColor = Color.Transparent.toArgb()
        insetsController.isAppearanceLightNavigationBars = !useDarkIcons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }
}