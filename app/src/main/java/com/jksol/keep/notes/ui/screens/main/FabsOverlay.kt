package com.jksol.keep.notes.ui.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jksol.keep.notes.ui.theme.SemiGray

@Composable
fun FabsOverlay(enabled: Boolean, onClick: () -> Unit) {
    val overlayColor by animateColorAsState(
        targetValue = if (enabled) SemiGray.copy(alpha = 0.6f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "OverlayFade"
    )

    if (overlayColor != Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                )
        )
    }
}