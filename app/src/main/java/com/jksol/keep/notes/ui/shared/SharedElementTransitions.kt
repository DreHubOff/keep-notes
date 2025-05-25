@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.jksol.keep.notes.ui.shared

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.animatedSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.contentSize
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

val LocalSharedTransitionSettings = compositionLocalOf<SharedTransitionSettings?> { null }

@Composable
fun Modifier.sharedBoundsTransition(
    transitionKey: Any,
    boundsTransform: BoundsTransform = BoundsTransform { _, _ -> tween(durationMillis = 600) },
    enter: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 300)),
    exit: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 300)),
    resizeMode: ResizeMode = ResizeMode.RemeasureToBounds,
    placeHolderSize: PlaceHolderSize = animatedSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier {
    val settings = LocalSharedTransitionSettings.current ?: return this
    return with(settings.transitionScope) {
        sharedBounds(
            sharedContentState = rememberSharedContentState(key = transitionKey),
            animatedVisibilityScope = settings.animationScope,
            boundsTransform = boundsTransform,
            enter = enter,
            exit = exit,
            resizeMode = resizeMode,
            placeHolderSize = placeHolderSize,
            renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            zIndexInOverlay = zIndexInOverlay,
        )
    }
}

@Composable
fun Modifier.sharedElementTransition(
    transitionKey: Any,
    boundsTransform: BoundsTransform = BoundsTransform { _, _ -> tween(durationMillis = 500) },
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier {
    val settings = LocalSharedTransitionSettings.current ?: return this
    return with(settings.transitionScope) {
        sharedElement(
            sharedContentState = rememberSharedContentState(key = transitionKey),
            animatedVisibilityScope = settings.animationScope,
            boundsTransform = boundsTransform,
            placeHolderSize = placeHolderSize,
            renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            zIndexInOverlay = zIndexInOverlay,
        )
    }
}